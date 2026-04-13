package com.example.AlumniPortal.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class MentorshipRequestDTO {

    @NotBlank(message = "Area of interest is required")
    @Size(max = 120, message = "Area of interest must be at most 120 characters")
    private String areaOfInterest;

    @NotBlank(message = "Message is required")
    @Size(max = 2000, message = "Message must be at most 2000 characters")
    private String message;

    @NotNull(message = "Please choose an availability slot")
    private Long availabilitySlotId;

    @NotNull(message = "Please select a requested date")
    @FutureOrPresent(message = "Requested date must be today or later")
    private LocalDate requestedDate;

    @NotNull(message = "Duration is required")
    @Min(value = 15, message = "Duration must be at least 15 minutes")
    @Max(value = 180, message = "Duration must be at most 180 minutes")
    private Integer durationMinutes;

    @NotBlank(message = "Session mode is required")
    private String sessionMode;
}
