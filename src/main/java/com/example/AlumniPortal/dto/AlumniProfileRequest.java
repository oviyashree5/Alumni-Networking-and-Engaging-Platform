package com.example.AlumniPortal.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AlumniProfileRequest {

    @Size(max = 120, message = "Name must be at most 120 characters")
    private String name;

    @Size(max = 10, message = "Batch year must be at most 10 characters")
    private String batchYear;

    @Size(max = 120, message = "Department must be at most 120 characters")
    private String department;

    @Size(max = 500, message = "Skills must be at most 500 characters")
    private String skills;

    @Size(max = 120, message = "Profession must be at most 120 characters")
    private String profession;

    @Size(max = 120, message = "Location must be at most 120 characters")
    private String location;

    @Size(max = 120, message = "Contact must be at most 120 characters")
    private String contact;

    @Pattern(
            regexp = "^(|(https?://)?(www\\.)?linkedin\\.com/.*)$",
            message = "LinkedIn URL must be a valid linkedin.com link"
    )
    private String linkedinUrl;

    private String profilePhoto;
}
