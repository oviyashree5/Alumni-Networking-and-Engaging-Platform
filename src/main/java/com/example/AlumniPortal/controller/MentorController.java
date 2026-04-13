package com.example.AlumniPortal.controller;

import com.example.AlumniPortal.dto.*;
import com.example.AlumniPortal.security.CustomUserDetails;
import com.example.AlumniPortal.service.MentorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mentorship")
@RequiredArgsConstructor
public class MentorController {

    private final MentorService mentorService;

    @PostMapping("/become")
    @PreAuthorize("hasRole('ALUMNI')")
    public MentorDiscoveryResponse becomeMentor(
            @AuthenticationPrincipal CustomUserDetails user,
            @Valid @RequestBody MentorProfileRequest request) {

        return mentorService.createOrUpdateProfile(user.getUserId(), request);
    }

    @PostMapping("/availability")
    @PreAuthorize("hasRole('ALUMNI')")
    public MentorAvailabilityResponse addAvailability(
            @AuthenticationPrincipal CustomUserDetails user,
            @Valid @RequestBody MentorAvailabilityRequest request) {

        return mentorService.addAvailability(user.getUserId(), request);
    }

    @GetMapping("/availability/{mentorId}")
    public List<MentorAvailabilityResponse> getAvailability(@PathVariable Long mentorId) {
        return mentorService.getAvailability(mentorId);
    }

    @GetMapping("/mentors")
    public List<MentorDiscoveryResponse> getMentors(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String domain,
            @RequestParam(required = false) String company,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) Integer experience) {

        try {
            return mentorService.getMentors(search, domain, company, location, experience);
        } catch (Exception e) {
            e.printStackTrace(); // 🔥 VERY IMPORTANT
            throw new RuntimeException("Error fetching mentors: " + e.getMessage());
        }
    }

    @GetMapping("/mentors/top")
    public List<MentorDiscoveryResponse> getTopMentors() {
        return mentorService.getTopMentors();
    }

    @GetMapping("/mentors/new")
    public List<MentorDiscoveryResponse> getNewMentors() {
        return mentorService.getNewMentors();
    }

    @PostMapping("/request/{mentorId}")
    @PreAuthorize("hasRole('ALUMNI')")
    public MentorshipRequestResponse sendRequest(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Long mentorId,
            @Valid @RequestBody MentorshipRequestDTO dto) {

        return mentorService.sendRequest(user.getUserId(), mentorId, dto);
    }

    @PutMapping("/status/{requestId}")
    @PreAuthorize("hasRole('ALUMNI')")
    public MentorshipRequestResponse updateStatus(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Long requestId,
            @RequestParam String status) {

        return mentorService.updateStatus(user.getUserId(), requestId, status);
    }

    @GetMapping("/my-requests")
    @PreAuthorize("hasRole('ALUMNI')")
    public List<MentorshipRequestResponse> myRequests(
            @AuthenticationPrincipal CustomUserDetails user) {

        return mentorService.getMyRequests(user.getUserId());
    }

    @GetMapping("/received")
    @PreAuthorize("hasRole('ALUMNI')")
    public List<MentorshipRequestResponse> received(
            @AuthenticationPrincipal CustomUserDetails user) {

        return mentorService.getRequestsForMentor(user.getUserId());
    }

    @GetMapping("/my-mentorships")
    @PreAuthorize("hasRole('ALUMNI')")
    public List<MentorshipSessionResponse> getMyMentorships(
            @AuthenticationPrincipal CustomUserDetails user) {

        return mentorService.getMyMentorships(user.getUserId());
    }

    @PutMapping("/sessions/{sessionId}/details")
    @PreAuthorize("hasRole('ALUMNI')")
    public MentorshipSessionResponse updateSessionDetails(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Long sessionId,
            @Valid @RequestBody MentorshipSessionUpdateRequest request) {

        return mentorService.updateSessionDetails(user.getUserId(), sessionId, request);
    }

    @PutMapping("/sessions/{sessionId}/complete")
    @PreAuthorize("hasRole('ALUMNI')")
    public MentorshipSessionResponse completeSession(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Long sessionId) {

        return mentorService.completeSession(user.getUserId(), sessionId);
    }

    @PostMapping("/feedback")
    @PreAuthorize("hasRole('ALUMNI')")
    public String submitFeedback(
            @AuthenticationPrincipal CustomUserDetails user,
            @Valid @RequestBody MentorFeedbackRequest request) {

        return mentorService.submitFeedback(user.getUserId(), request);
    }

    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('ALUMNI')")
    public MentorshipDashboardResponse getDashboard(
            @AuthenticationPrincipal CustomUserDetails user) {

        return mentorService.getDashboard(user.getUserId());
    }
}
