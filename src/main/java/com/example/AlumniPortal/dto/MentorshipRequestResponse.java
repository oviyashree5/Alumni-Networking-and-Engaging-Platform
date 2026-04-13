package com.example.AlumniPortal.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class MentorshipRequestResponse {

    private Long id;
    private Long mentorId;
    private String mentorName;
    private String mentorEmail;
    private Long menteeId;
    private String menteeName;
    private String areaOfInterest;
    private String message;
    private String status;
    private String mentorResponse;
    private LocalDate requestedDate;
    private Integer durationMinutes;
    private String sessionMode;
    private MentorAvailabilityResponse availabilitySlot;
    private LocalDateTime createdAt;
    private LocalDateTime respondedAt;
}
