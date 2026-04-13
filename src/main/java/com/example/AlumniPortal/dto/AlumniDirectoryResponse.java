package com.example.AlumniPortal.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AlumniDirectoryResponse {

    private Long userId;
    private String name;
    private String batchYear;
    private String department;
    private String skills;
    private String profession;
    private String location;
    private String linkedinUrl;
}
