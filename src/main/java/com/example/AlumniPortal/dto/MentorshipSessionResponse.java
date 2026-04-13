package com.example.AlumniPortal.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class MentorshipSessionResponse {

    private Long id;
    private Long requestId;
    private Long mentorId;
    private String mentorName;
    private Long menteeId;
    private String menteeName;
    private LocalDateTime scheduledAt;
    private Integer durationMinutes;
    private String sessionMode;
    private String status;
    private String meetingPlatform;
    private String meetingLink;
    private String meetingNotes;
    private LocalDateTime completedAt;
    private boolean feedbackSubmitted;
}
