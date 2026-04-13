package com.example.AlumniPortal.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class MentorDiscoveryResponse {

    private Long mentorProfileId;
    private Long mentorUserId;
    private String name;
    private String email;
    private String company;
    private String designation;
    private String location;
    private String domain;
    private Integer yearsExperience;
    private String photoUrl;
    private String bio;
    private List<String> expertiseAreas;
    private Double ratingAverage;
    private Integer totalReviews;
    private Integer sessionsCompleted;
    private List<String> badges;
    private LocalDateTime createdAt;
}
