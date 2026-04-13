package com.example.AlumniPortal.controller;

import com.example.AlumniPortal.dto.LoginRequest;
import com.example.AlumniPortal.dto.LoginResponse;
import com.example.AlumniPortal.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }
}
