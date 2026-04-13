package com.example.AlumniPortal.dto;

import com.example.AlumniPortal.entity.VerificationStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private Long userId;
    private String email;
    private String role;
    private VerificationStatus verificationStatus;
    private boolean profileCompleted;
    private boolean firstTimeLogin;
}
