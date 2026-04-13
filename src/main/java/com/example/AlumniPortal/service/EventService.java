package com.example.AlumniPortal.service;

import com.example.AlumniPortal.dto.EventRequest;
import com.example.AlumniPortal.dto.EventResponse;
import com.example.AlumniPortal.entity.AlumniProfile;
import com.example.AlumniPortal.entity.Event;
import com.example.AlumniPortal.entity.EventRegistration;
import com.example.AlumniPortal.exception.BadRequestException;
import com.example.AlumniPortal.exception.ResourceNotFoundException;
import com.example.AlumniPortal.repository.AlumniProfileRepository;
import com.example.AlumniPortal.repository.EventRegistrationRepository;
import com.example.AlumniPortal.repository.EventRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
public class EventService {

    private final EventRepository eventRepo;
    private final EventRegistrationRepository registrationRepo;
    private final AlumniProfileRepository alumniProfileRepository;

    public EventService(EventRepository eventRepo,
                        EventRegistrationRepository registrationRepo,
                        AlumniProfileRepository alumniProfileRepository) {
        this.eventRepo = eventRepo;
        this.registrationRepo = registrationRepo;
        this.alumniProfileRepository = alumniProfileRepository;
    }

    @Transactional
    public EventResponse createEvent(EventRequest request) {
        Event event = new Event();
        applyRequest(event, request);
        event.setRegisteredCount(0);
        return mapToResponse(eventRepo.save(event));
    }

    @Transactional
    public EventResponse updateEvent(Long id, EventRequest request) {
        Event event = eventRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));

        applyRequest(event, request);
        return mapToResponse(eventRepo.save(event));
    }

    @Transactional
    public void deleteEvent(Long id) {
        eventRepo.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<EventResponse> getAllEventsForAdmin() {
        return eventRepo.findAll()
                .stream()
                .sorted(eventNewestFirst())
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<EventResponse> getEventsForAlumni(Long userId) {
        String batchYear = alumniProfileRepository.findByUserId(userId)
                .map(AlumniProfile::getBatchYear)
                .orElse(null);

        return eventRepo.findAll()
                .stream()
                .filter(event -> isVisibleToBatch(event, batchYear))
                .sorted(eventNewestFirst())
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public EventResponse getEventById(Long id) {
        Event event = eventRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));
        return mapToResponse(event);
    }

    @Transactional
    public String registerForEvent(Long eventId, Long userId) {
        Event event = eventRepo.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));

        String batchYear = alumniProfileRepository.findByUserId(userId)
                .map(AlumniProfile::getBatchYear)
                .orElse(null);

        if (!isVisibleToBatch(event, batchYear)) {
            throw new BadRequestException("This event is not available for your batch");
        }

        if (registrationRepo.findByEventIdAndUserId(eventId, userId).isPresent()) {
            return "You already registered for this event";
        }

        if (event.getMaxSeats() != null && event.getRegisteredCount() >= event.getMaxSeats()) {
            throw new BadRequestException("Event is full");
        }

        EventRegistration registration = new EventRegistration();
        registration.setUserId(userId);
        registration.setEvent(event);
        registrationRepo.save(registration);

        event.setRegisteredCount((event.getRegisteredCount() == null ? 0 : event.getRegisteredCount()) + 1);
        eventRepo.save(event);

        return "Registered successfully";
    }

    @Transactional(readOnly = true)
    public List<EventRegistration> getMyRegistrations(Long userId) {
        return registrationRepo.findByUserId(userId);
    }

    @Transactional(readOnly = true)
    public List<EventRegistration> getEventRegistrations(Long eventId) {
        return registrationRepo.findByEventId(eventId);
    }

    private void applyRequest(Event event, EventRequest request) {
        String audienceType = normalizeAudienceType(request.getAudienceType());

        event.setTitle(request.getTitle());
        event.setDescription(request.getDescription());
        event.setLocation(request.getLocation());
        event.setEventDate(request.getEventDate());
        event.setMaxSeats(request.getMaxSeats());
        event.setAudienceType(audienceType);
        event.setTargetBatchYear(
                "SPECIFIC_BATCH".equals(audienceType)
                        ? normalizeBatchYear(request.getTargetBatchYear())
                        : null
        );

        if ("SPECIFIC_BATCH".equals(event.getAudienceType())
                && (event.getTargetBatchYear() == null || event.getTargetBatchYear().isBlank())) {
            throw new BadRequestException("Target batch year is required for specific batch events");
        }
    }

    private String normalizeAudienceType(String audienceType) {
        String normalized = audienceType == null ? "ALL" : audienceType.trim().toUpperCase();
        if (!List.of("ALL", "SPECIFIC_BATCH").contains(normalized)) {
            throw new BadRequestException("Audience type must be ALL or SPECIFIC_BATCH");
        }
        return normalized;
    }

    private boolean isVisibleToBatch(Event event, String batchYear) {
        String audienceType = normalizeStoredAudienceType(event.getAudienceType());
        String targetBatchYear = normalizeBatchYear(event.getTargetBatchYear());
        String currentBatchYear = normalizeBatchYear(batchYear);

        return "ALL".equals(audienceType)
                || (currentBatchYear != null && currentBatchYear.equals(targetBatchYear));
    }

    private String normalizeStoredAudienceType(String audienceType) {
        if (audienceType == null || audienceType.isBlank()) {
            return "ALL";
        }
        return audienceType.trim().toUpperCase();
    }

    private String normalizeBatchYear(String batchYear) {
        if (batchYear == null || batchYear.isBlank()) {
            return null;
        }
        return batchYear.trim();
    }

    private EventResponse mapToResponse(Event event) {
        return new EventResponse(
                event.getId(),
                event.getTitle(),
                event.getDescription(),
                event.getLocation(),
                event.getEventDate(),
                event.getMaxSeats(),
                event.getRegisteredCount(),
                normalizeStoredAudienceType(event.getAudienceType()),
                normalizeBatchYear(event.getTargetBatchYear())
        );
    }

    private Comparator<Event> eventNewestFirst() {
        return Comparator
                .comparing(Event::getCreatedAt, Comparator.nullsLast(Comparator.reverseOrder()))
                .thenComparing(Event::getId, Comparator.nullsLast(Comparator.reverseOrder()));
    }
}
