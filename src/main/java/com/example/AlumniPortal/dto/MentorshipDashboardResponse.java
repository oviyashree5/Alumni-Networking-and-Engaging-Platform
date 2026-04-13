package com.example.AlumniPortal.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MentorshipDashboardResponse {

    private long activeMentorships;
    private long pendingRequests;
    private long requestsToReview;
    private long completedSessions;
}
