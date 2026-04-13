package com.example.AlumniPortal.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AlumniProfileResponse {

    private Long userId;
    private String name;
    private String batchYear;
    private String department;
    private String skills;
    private String profession;
    private String location;
    private String contact;
    private String linkedinUrl;
    private String profilePhoto;
}
