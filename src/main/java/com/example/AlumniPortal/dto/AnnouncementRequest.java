package com.example.AlumniPortal.dto;

import lombok.Data;

@Data
public class AnnouncementRequest {

    private String title;
    private String content;
    private String category;
    private String priority;
}
