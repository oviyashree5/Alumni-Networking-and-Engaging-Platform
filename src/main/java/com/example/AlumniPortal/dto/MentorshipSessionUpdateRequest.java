package com.example.AlumniPortal.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MentorshipSessionUpdateRequest {

    @NotBlank(message = "Topic is required")
    private String topic;

    private String description;

    @NotBlank(message = "Meeting link is required")
    private String meetingLink;

    @Future(message = "Session time must be in the future")
    private LocalDateTime scheduledAt;

    // ✅ NEW FIELDS (based on your getters)
    private String meetingPlatform;   // Zoom / Google Meet
    private String meetingNotes;      // Notes after session

}