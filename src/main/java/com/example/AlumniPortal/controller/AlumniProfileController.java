package com.example.AlumniPortal.controller;

import com.example.AlumniPortal.dto.AlumniProfileRequest;
import com.example.AlumniPortal.dto.AlumniProfileResponse;
import com.example.AlumniPortal.security.CustomUserDetails;
import com.example.AlumniPortal.service.AlumniProfileService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
public class AlumniProfileController {

    private final AlumniProfileService service;

    public AlumniProfileController(AlumniProfileService service) {
        this.service = service;
    }

    @GetMapping("/me")
    public AlumniProfileResponse getMyProfile() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails user = (CustomUserDetails) auth.getPrincipal();
        return service.getProfileByUserId(user.getUserId());
    }

    @PutMapping("/me")
    public AlumniProfileResponse updateMyProfile(
            @Valid @RequestBody AlumniProfileRequest request) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails user = (CustomUserDetails) auth.getPrincipal();
        return service.updateProfile(user.getUserId(), request);
    }

    @GetMapping("/admin/{userId}")
    public AlumniProfileResponse getProfileByAdmin(@PathVariable Long userId) {
        return service.getProfileByUserId(userId);
    }
}
