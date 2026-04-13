package com.example.AlumniPortal.service;

import com.example.AlumniPortal.dto.*;
import com.example.AlumniPortal.entity.*;
import com.example.AlumniPortal.exception.BadRequestException;
import com.example.AlumniPortal.exception.ResourceNotFoundException;
import com.example.AlumniPortal.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class MentorService {

    private final MentorProfileRepository mentorProfileRepository;
    private final MentorshipRequestRepository mentorshipRequestRepository;
    private final UserRepository userRepository;
    private final AlumniProfileRepository alumniProfileRepository;
    private final MentorAvailabilityRepository mentorAvailabilityRepository;
    private final MentorshipSessionRepository mentorshipSessionRepository;
    private final MentorFeedbackRepository mentorFeedbackRepository;

    @CacheEvict(value = "mentorDiscovery", allEntries = true)
    @Transactional
    public MentorDiscoveryResponse createOrUpdateProfile(Long userId, MentorProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<String> expertiseAreas = request.getExpertiseAreas() == null
                ? List.of()
                : request.getExpertiseAreas().stream()
                .map(String::trim)
                .filter(skill -> !skill.isBlank())
                .distinct()
                .toList();

        if (expertiseAreas.isEmpty()) {
            throw new BadRequestException("At least one expertise area is required");
        }

        MentorProfile profile = mentorProfileRepository.findByUserId(userId)
                .orElseGet(MentorProfile::new);

        profile.setUser(user);
        profile.setCompany(request.getCompany().trim());
        profile.setDesignation(request.getDesignation().trim());
        profile.setLocation(request.getLocation().trim());
        profile.setDomain(request.getDomain().trim());
        profile.setYearsExperience(request.getYearsExperience());
        profile.setPhotoUrl(isBlank(request.getPhotoUrl()) ? null : request.getPhotoUrl().trim());
        profile.setBio(request.getBio().trim());
        profile.setExpertiseAreas(expertiseAreas);
        profile.setActive(true);
        profile.setRatingAverage(profile.getRatingAverage() == null ? 0.0 : profile.getRatingAverage());
        profile.setTotalReviews(profile.getTotalReviews() == null ? 0 : profile.getTotalReviews());
        profile.setSessionsCompleted(profile.getSessionsCompleted() == null ? 0 : profile.getSessionsCompleted());

        return mapMentorProfile(mentorProfileRepository.save(profile));
    }

    @CacheEvict(value = "mentorDiscovery", allEntries = true)
    @Transactional
    public MentorAvailabilityResponse addAvailability(Long userId, MentorAvailabilityRequest request) {
        MentorProfile profile = mentorProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Please create a mentor profile first"));

        if (!request.getEndTime().isAfter(request.getStartTime())) {
            throw new BadRequestException("End time must be after start time");
        }

        MentorAvailability availability = MentorAvailability.builder()
                .mentorProfile(profile)
                .dayOfWeek(request.getDayOfWeek())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .active(true)
                .build();

        return mapAvailability(mentorAvailabilityRepository.save(availability));
    }

    @Transactional(readOnly = true)
    public List<MentorAvailabilityResponse> getAvailability(Long mentorId) {
        return mentorAvailabilityRepository.findByMentorProfileUserIdAndActiveTrueOrderByDayOfWeekAscStartTimeAsc(mentorId)
                .stream()
                .map(this::mapAvailability)
                .toList();
    }

    @Cacheable(
            value = "mentorDiscovery",
            key = "T(java.util.Objects).toString(#search,'') + '|' + T(java.util.Objects).toString(#domain,'') + '|' + T(java.util.Objects).toString(#company,'') + '|' + T(java.util.Objects).toString(#location,'') + '|' + T(java.util.Objects).toString(#experience,'')"
    )
    @Transactional(readOnly = true)
    public List<MentorDiscoveryResponse> getMentors(String search,
                                                    String domain,
                                                    String company,
                                                    String location,
                                                    Integer experience) {
        return mentorProfileRepository.findByActiveTrue()
                .stream()
                .filter(profile -> matches(profile, search, domain, company, location, experience))
                .sorted(Comparator
                        .comparing(MentorProfile::getRatingAverage, Comparator.nullsLast(Comparator.reverseOrder()))
                        .thenComparing(MentorProfile::getSessionsCompleted, Comparator.nullsLast(Comparator.reverseOrder()))
                        .thenComparing(MentorProfile::getCreatedAt, Comparator.nullsLast(Comparator.reverseOrder())))
                .map(this::mapMentorProfile)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<MentorDiscoveryResponse> getTopMentors() {
        return getMentors(null, null, null, null, null)
                .stream()
                .sorted(Comparator
                        .comparing(MentorDiscoveryResponse::getRatingAverage, Comparator.nullsLast(Comparator.reverseOrder()))
                        .thenComparing(MentorDiscoveryResponse::getSessionsCompleted, Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(3)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<MentorDiscoveryResponse> getNewMentors() {
        return getMentors(null, null, null, null, null)
                .stream()
                .sorted(Comparator.comparing(MentorDiscoveryResponse::getCreatedAt, Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(3)
                .toList();
    }

    @Transactional
    public MentorshipRequestResponse sendRequest(Long menteeId, Long mentorId, MentorshipRequestDTO dto) {
        if (menteeId.equals(mentorId)) {
            throw new BadRequestException("You cannot request mentorship from yourself");
        }

        User mentor = userRepository.findById(mentorId)
                .orElseThrow(() -> new ResourceNotFoundException("Mentor not found"));
        User mentee = userRepository.findById(menteeId)
                .orElseThrow(() -> new ResourceNotFoundException("Mentee not found"));

        MentorProfile mentorProfile = mentorProfileRepository.findByUserId(mentorId)
                .orElseThrow(() -> new ResourceNotFoundException("Selected mentor profile not found"));

        MentorAvailability slot = mentorAvailabilityRepository.findById(dto.getAvailabilitySlotId())
                .orElseThrow(() -> new ResourceNotFoundException("Availability slot not found"));

        if (!Objects.equals(slot.getMentorProfile().getId(), mentorProfile.getId()) || !slot.isActive()) {
            throw new BadRequestException("Selected availability slot is not valid for this mentor");
        }

        validateRequestedSchedule(dto.getRequestedDate(), slot);

        boolean hasOpenRequest = mentorshipRequestRepository.findByMenteeIdAndMentorId(menteeId, mentorId)
                .stream()
                .anyMatch(request -> List.of("PENDING", "ACCEPTED").contains(request.getStatus()));

        if (hasOpenRequest) {
            throw new BadRequestException("You already have an active mentorship request with this mentor");
        }

        LocalDate requestedDate = dto.getRequestedDate() == null ? LocalDate.now() : dto.getRequestedDate();
        long weeklyRequestCount = mentorshipRequestRepository.findByMenteeId(menteeId)
                .stream()
                .filter(existingRequest -> isWithinSameWeek(existingRequest.getRequestedDate(), requestedDate))
                .filter(existingRequest -> !"REJECTED".equalsIgnoreCase(existingRequest.getStatus()))
                .count();

        if (weeklyRequestCount >= 2) {
            throw new BadRequestException("You can request only 2 mentorship sessions per week");
        }

        MentorshipRequest request = MentorshipRequest.builder()
                .mentor(mentor)
                .mentee(mentee)
                .availabilitySlot(slot)
                .areaOfInterest(dto.getAreaOfInterest().trim())
                .message(dto.getMessage().trim())
                .requestedDate(requestedDate)
                .durationMinutes(dto.getDurationMinutes())
                .sessionMode(dto.getSessionMode().trim())
                .status("PENDING")
                .createdAt(LocalDateTime.now())
                .build();

        return mapRequest(mentorshipRequestRepository.save(request));
    }

    @CacheEvict(value = "mentorDiscovery", allEntries = true)
    @Transactional
    public MentorshipRequestResponse updateStatus(Long mentorUserId, Long requestId, String status) {
        MentorshipRequest request = mentorshipRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Request not found"));

        if (!Objects.equals(request.getMentor().getId(), mentorUserId)) {
            throw new BadRequestException("You can only manage your own mentorship requests");
        }

        if (!List.of("ACCEPTED", "REJECTED").contains(status)) {
            throw new BadRequestException("Invalid status");
        }

        request.setStatus(status);
        request.setRespondedAt(LocalDateTime.now());
        mentorshipRequestRepository.save(request);

        if ("ACCEPTED".equals(status) && mentorshipSessionRepository.findByRequestId(request.getId()).isEmpty()) {
            LocalDateTime scheduledAt = resolveScheduledAt(request);

            MentorshipSession session = MentorshipSession.builder()
                    .request(request)
                    .mentor(request.getMentor())
                    .mentee(request.getMentee())
                    .scheduledAt(scheduledAt)
                    .durationMinutes(request.getDurationMinutes())
                    .sessionMode(request.getSessionMode())
                    .status("SCHEDULED")
                    .build();

            mentorshipSessionRepository.save(session);
        }

        return mapRequest(request);
    }

    @Transactional(readOnly = true)
    public List<MentorshipRequestResponse> getMyRequests(Long userId) {
        return mentorshipRequestRepository.findByMenteeId(userId)
                .stream()
                .sorted(Comparator.comparing(MentorshipRequest::getCreatedAt).reversed())
                .map(this::mapRequest)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<MentorshipRequestResponse> getRequestsForMentor(Long mentorId) {
        return mentorshipRequestRepository.findByMentorId(mentorId)
                .stream()
                .sorted(Comparator.comparing(MentorshipRequest::getCreatedAt).reversed())
                .map(this::mapRequest)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<MentorshipSessionResponse> getMyMentorships(Long userId) {
        return mentorshipSessionRepository.findByMenteeIdOrMentorIdOrderByScheduledAtDesc(userId, userId)
                .stream()
                .map(session -> mapSession(session, userId))
                .toList();
    }

    @CacheEvict(value = "mentorDiscovery", allEntries = true)
    @Transactional
    public MentorshipSessionResponse updateSessionDetails(Long mentorId,
                                                          Long sessionId,
                                                          MentorshipSessionUpdateRequest request) {
        MentorshipSession session = mentorshipSessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Session not found"));

        if (!Objects.equals(session.getMentor().getId(), mentorId)) {
            throw new BadRequestException("Only the mentor can update session details");
        }

        if (!"SCHEDULED".equals(session.getStatus())) {
            throw new BadRequestException("Only scheduled sessions can be updated");
        }

        if ("Virtual".equalsIgnoreCase(session.getSessionMode())
                && (request.getMeetingLink() == null || request.getMeetingLink().isBlank())) {
            throw new BadRequestException("Meeting link is required for virtual sessions");
        }

        session.setMeetingPlatform(request.getMeetingPlatform().trim());
        session.setMeetingLink(blankToNull(request.getMeetingLink()));
        session.setMeetingNotes(blankToNull(request.getMeetingNotes()));
        mentorshipSessionRepository.save(session);

        return mapSession(session, mentorId);
    }

    @CacheEvict(value = "mentorDiscovery", allEntries = true)
    @Transactional
    public MentorshipSessionResponse completeSession(Long mentorId, Long sessionId) {
        MentorshipSession session = mentorshipSessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Session not found"));

        if (!Objects.equals(session.getMentor().getId(), mentorId)) {
            throw new BadRequestException("Only the mentor can complete the session");
        }

        if (!"SCHEDULED".equals(session.getStatus())) {
            throw new BadRequestException("Only scheduled sessions can be completed");
        }

        session.setStatus("COMPLETED");
        session.setCompletedAt(LocalDateTime.now());
        mentorshipSessionRepository.save(session);

        MentorProfile profile = mentorProfileRepository.findByUserId(mentorId)
                .orElseThrow(() -> new ResourceNotFoundException("Mentor profile not found"));
        profile.setSessionsCompleted((profile.getSessionsCompleted() == null ? 0 : profile.getSessionsCompleted()) + 1);
        mentorProfileRepository.save(profile);

        return mapSession(session, mentorId);
    }

    @CacheEvict(value = "mentorDiscovery", allEntries = true)
    @Transactional
    public String submitFeedback(Long menteeId, MentorFeedbackRequest request) {
        MentorshipSession session = mentorshipSessionRepository.findById(request.getSessionId())
                .orElseThrow(() -> new ResourceNotFoundException("Session not found"));

        if (!Objects.equals(session.getMentee().getId(), menteeId)) {
            throw new BadRequestException("Only the mentee can submit feedback");
        }

        if (!"COMPLETED".equals(session.getStatus())) {
            throw new BadRequestException("Feedback can only be submitted for completed sessions");
        }

        if (mentorFeedbackRepository.findBySessionId(session.getId()).isPresent()) {
            throw new BadRequestException("Feedback already submitted for this session");
        }

        MentorFeedback feedback = MentorFeedback.builder()
                .session(session)
                .mentor(session.getMentor())
                .mentee(session.getMentee())
                .rating(request.getRating())
                .comment(request.getComment() == null ? null : request.getComment().trim())
                .build();

        mentorFeedbackRepository.save(feedback);

        MentorProfile profile = mentorProfileRepository.findByUserId(session.getMentor().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Mentor profile not found"));

        List<MentorFeedback> allFeedback = mentorFeedbackRepository.findByMentorId(session.getMentor().getId());
        double average = allFeedback.stream()
                .mapToInt(MentorFeedback::getRating)
                .average()
                .orElse(0.0);

        profile.setRatingAverage(average);
        profile.setTotalReviews(allFeedback.size());
        mentorProfileRepository.save(profile);

        return "Feedback submitted successfully";
    }

    @Transactional(readOnly = true)
    public MentorshipDashboardResponse getDashboard(Long userId) {
        List<MentorshipRequest> sent = mentorshipRequestRepository.findByMenteeId(userId);
        List<MentorshipRequest> received = mentorshipRequestRepository.findByMentorId(userId);
        List<MentorshipSession> sessions = mentorshipSessionRepository.findByMenteeIdOrMentorIdOrderByScheduledAtDesc(userId, userId);

        long activeMentorships = sessions.stream()
                .filter(session -> "SCHEDULED".equals(session.getStatus()))
                .count();
        long pendingRequests = sent.stream()
                .filter(request -> "PENDING".equals(request.getStatus()))
                .count();
        long requestsToReview = received.stream()
                .filter(request -> "PENDING".equals(request.getStatus()))
                .count();
        long completedSessions = sessions.stream()
                .filter(session -> "COMPLETED".equals(session.getStatus()))
                .count();

        return new MentorshipDashboardResponse(
                activeMentorships,
                pendingRequests,
                requestsToReview,
                completedSessions
        );
    }

    private boolean matches(MentorProfile profile,
                            String search,
                            String domain,
                            String company,
                            String location,
                            Integer experience) {
        String name = getDisplayName(profile.getUser().getId());
        return contains(name, search)
                && contains(profile.getDomain(), domain)
                && contains(profile.getCompany(), company)
                && contains(profile.getLocation(), location)
                && (experience == null || (profile.getYearsExperience() != null && profile.getYearsExperience() >= experience));
    }

    private boolean contains(String value, String filter) {
        if (filter == null || filter.isBlank()) {
            return true;
        }
        return value != null && value.toLowerCase().contains(filter.trim().toLowerCase());
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private String blankToNull(String value) {
        return isBlank(value) ? null : value.trim();
    }

    private MentorDiscoveryResponse mapMentorProfile(MentorProfile profile) {
        String name = getDisplayName(profile.getUser().getId());
        String email = profile.getUser().getEmail();
        Double rating = profile.getRatingAverage() == null ? 0.0 : profile.getRatingAverage();
        Integer reviews = profile.getTotalReviews() == null ? 0 : profile.getTotalReviews();
        Integer sessions = profile.getSessionsCompleted() == null ? 0 : profile.getSessionsCompleted();
        List<String> expertiseAreas = profile.getExpertiseAreas() == null
                ? List.of()
                : List.copyOf(profile.getExpertiseAreas());

        return MentorDiscoveryResponse.builder()
                .mentorProfileId(profile.getId())
                .mentorUserId(profile.getUser().getId())
                .name(name)
                .email(email)
                .company(profile.getCompany())
                .designation(profile.getDesignation())
                .location(profile.getLocation())
                .domain(profile.getDomain())
                .yearsExperience(profile.getYearsExperience())
                .photoUrl(profile.getPhotoUrl())
                .bio(profile.getBio())
                .expertiseAreas(expertiseAreas)
                .ratingAverage(rating)
                .totalReviews(reviews)
                .sessionsCompleted(sessions)
                .badges(resolveBadges(profile, rating, reviews, sessions))
                .createdAt(profile.getCreatedAt())
                .build();
    }

    private List<String> resolveBadges(MentorProfile profile,
                                       Double rating,
                                       Integer reviews,
                                       Integer sessions) {
        java.util.ArrayList<String> badges = new java.util.ArrayList<>();
        if (sessions > 0) {
            badges.add("Active Mentor");
        }
        if (reviews > 0 && rating >= 4.5) {
            badges.add("Top Rated");
        }
        if (profile.getCreatedAt() != null && profile.getCreatedAt().isAfter(LocalDateTime.now().minusDays(45))) {
            badges.add("New Mentor");
        }
        return badges;
    }

    private MentorshipRequestResponse mapRequest(MentorshipRequest request) {
        Long mentorId = request.getMentor() != null ? request.getMentor().getId() : null;
        Long menteeId = request.getMentee() != null ? request.getMentee().getId() : null;

        return MentorshipRequestResponse.builder()
                .id(request.getId())
                .mentorId(mentorId)
                .mentorName(mentorId == null ? "Mentor unavailable" : getDisplayName(mentorId))
                .mentorEmail(request.getMentor() == null ? null : request.getMentor().getEmail())
                .menteeId(menteeId)
                .menteeName(menteeId == null ? "Mentee unavailable" : getDisplayName(menteeId))
                .areaOfInterest(request.getAreaOfInterest())
                .message(request.getMessage())
                .status(request.getStatus())
                .mentorResponse(request.getMentorResponse())
                .requestedDate(request.getRequestedDate())
                .durationMinutes(request.getDurationMinutes())
                .sessionMode(request.getSessionMode())
                .availabilitySlot(mapAvailability(request.getAvailabilitySlot()))
                .createdAt(request.getCreatedAt())
                .respondedAt(request.getRespondedAt())
                .build();
    }

    private MentorAvailabilityResponse mapAvailability(MentorAvailability availability) {
        if (availability == null) {
            return null;
        }

        return new MentorAvailabilityResponse(
                availability.getId(),
                availability.getDayOfWeek(),
                availability.getStartTime(),
                availability.getEndTime()
        );
    }

    private LocalDateTime resolveScheduledAt(MentorshipRequest request) {
        LocalDate date = request.getRequestedDate() == null ? LocalDate.now().plusDays(1) : request.getRequestedDate();
        LocalTime time = request.getAvailabilitySlot() != null && request.getAvailabilitySlot().getStartTime() != null
                ? request.getAvailabilitySlot().getStartTime()
                : LocalTime.of(9, 0);

        return LocalDateTime.of(date, time);
    }

    private void validateRequestedSchedule(LocalDate requestedDate, MentorAvailability slot) {
        if (requestedDate == null) {
            throw new BadRequestException("Please select a requested date");
        }

        if (slot == null || slot.getDayOfWeek() == null || slot.getStartTime() == null) {
            throw new BadRequestException("Selected availability slot is invalid");
        }

        if (!requestedDate.getDayOfWeek().name().equalsIgnoreCase(slot.getDayOfWeek().name())) {
            throw new BadRequestException("Requested date must match the selected availability day");
        }

        LocalDateTime requestedDateTime = LocalDateTime.of(requestedDate, slot.getStartTime());
        if (!requestedDateTime.isAfter(LocalDateTime.now())) {
            throw new BadRequestException("Please choose a mentorship slot in the future");
        }
    }

    private boolean isWithinSameWeek(LocalDate existingDate, LocalDate requestedDate) {
        if (existingDate == null || requestedDate == null) {
            return false;
        }

        LocalDate startOfWeek = requestedDate.with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));
        LocalDate endOfWeek = requestedDate.with(TemporalAdjusters.nextOrSame(java.time.DayOfWeek.SUNDAY));
        return !existingDate.isBefore(startOfWeek) && !existingDate.isAfter(endOfWeek);
    }

    private MentorshipSessionResponse mapSession(MentorshipSession session, Long currentUserId) {
        boolean feedbackSubmitted = mentorFeedbackRepository.findBySessionId(session.getId()).isPresent();

        return MentorshipSessionResponse.builder()
                .id(session.getId())
                .requestId(session.getRequest().getId())
                .mentorId(session.getMentor().getId())
                .mentorName(getDisplayName(session.getMentor().getId()))
                .menteeId(session.getMentee().getId())
                .menteeName(getDisplayName(session.getMentee().getId()))
                .scheduledAt(session.getScheduledAt())
                .durationMinutes(session.getDurationMinutes())
                .sessionMode(session.getSessionMode())
                .status(session.getStatus())
                .meetingPlatform(session.getMeetingPlatform())
                .meetingLink(session.getMeetingLink())
                .meetingNotes(session.getMeetingNotes())
                .completedAt(session.getCompletedAt())
                .feedbackSubmitted(feedbackSubmitted && Objects.equals(session.getMentee().getId(), currentUserId))
                .build();
    }

    private String getDisplayName(Long userId) {
        return alumniProfileRepository.findByUserId(userId)
                .map(AlumniProfile::getName)
                .filter(name -> name != null && !name.isBlank() && !"Not Updated Yet".equalsIgnoreCase(name))
                .orElseGet(() -> userRepository.findById(userId).map(User::getEmail).orElse("User"));
    }
}
