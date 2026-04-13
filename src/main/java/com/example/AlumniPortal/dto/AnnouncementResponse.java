package com.example.AlumniPortal.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AnnouncementResponse {

    private Long id;
    private String title;
    private String content;
    private String category;
    private String priority;
    private String authorName;
    private LocalDateTime createdAt;
}
