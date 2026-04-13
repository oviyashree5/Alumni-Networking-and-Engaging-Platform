package com.example.AlumniPortal.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class EventResponse {

    private Long id;
    private String title;
    private String description;
    private String location;
    private LocalDateTime eventDate;
    private Integer maxSeats;
    private Integer registeredCount;
    private String audienceType;
    private String targetBatchYear;
}
