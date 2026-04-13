package com.example.AlumniPortal.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class MentorProfileRequest {

    @NotBlank(message = "Company is required")
    @Size(max = 120, message = "Company must be at most 120 characters")
    private String company;

    @NotBlank(message = "Designation is required")
    @Size(max = 120, message = "Designation must be at most 120 characters")
    private String designation;

    @NotBlank(message = "Location is required")
    @Size(max = 120, message = "Location must be at most 120 characters")
    private String location;

    @NotBlank(message = "Domain is required")
    @Size(max = 120, message = "Domain must be at most 120 characters")
    private String domain;

    @Min(value = 0, message = "Experience cannot be negative")
    @Max(value = 50, message = "Experience must be realistic")
    private Integer yearsExperience;

    @Pattern(
            regexp = "^(|(https?://).+)$",
            message = "Photo URL must be a valid URL"
    )
    private String photoUrl;

    @NotBlank(message = "Bio is required")
    @Size(max = 2000, message = "Bio must be at most 2000 characters")
    private String bio;

    private List<String> expertiseAreas;
}
