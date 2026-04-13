package com.example.AlumniPortal.dto;

import com.example.AlumniPortal.entity.VerificationStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserResponse {

    private Long id;
    private String email;
    private String role;
    private boolean verified;
    private VerificationStatus verificationStatus;
}
