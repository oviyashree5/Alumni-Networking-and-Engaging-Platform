package com.example.AlumniPortal.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class FeedbackResponse {

    private Long id;
    private Long userId;
    private String userName;
    private String userEmail;
    private String batchYear;
    private String department;
    private String message;
    private Integer rating;
    private LocalDateTime createdAt;
}
