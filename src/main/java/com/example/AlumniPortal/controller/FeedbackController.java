package com.example.AlumniPortal.controller;

import com.example.AlumniPortal.dto.FeedbackRequest;
import com.example.AlumniPortal.dto.FeedbackResponse;
import com.example.AlumniPortal.security.CustomUserDetails;
import com.example.AlumniPortal.service.FeedbackService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/feedback")
@RequiredArgsConstructor
public class FeedbackController {

    private final FeedbackService feedbackService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public FeedbackResponse submitFeedback(
            @Valid @RequestBody FeedbackRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        return feedbackService.submitFeedback(userDetails.getUserId(), request);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<FeedbackResponse> getAllFeedback() {
        return feedbackService.getAllFeedback();
    }
}
