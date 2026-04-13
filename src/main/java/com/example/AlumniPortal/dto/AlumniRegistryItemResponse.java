package com.example.AlumniPortal.dto;

import com.example.AlumniPortal.entity.AlumniRegistrationStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class AlumniRegistryItemResponse {

    private Long id;
    private String email;
    private String name;
    private String batchYear;
    private String department;
    private AlumniRegistrationStatus registrationStatus;
    private LocalDateTime importedAt;
    private LocalDateTime registeredAt;
}
