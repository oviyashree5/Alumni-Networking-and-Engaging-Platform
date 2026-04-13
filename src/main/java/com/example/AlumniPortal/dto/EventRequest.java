package com.example.AlumniPortal.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class EventRequest {

    @NotBlank(message = "Title is required")
    private String title;
    private String description;
    @NotBlank(message = "Location is required")
    private String location;
    @NotNull(message = "Event date is required")
    private LocalDateTime eventDate;
    private Integer maxSeats;
    @NotBlank(message = "Audience type is required")
    private String audienceType;
    private String targetBatchYear;
}
