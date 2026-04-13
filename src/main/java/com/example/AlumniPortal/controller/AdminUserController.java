package com.example.AlumniPortal.controller;

import com.example.AlumniPortal.dto.UserRequest;
import com.example.AlumniPortal.dto.UserResponse;
import com.example.AlumniPortal.entity.VerificationStatus;
import com.example.AlumniPortal.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    private final UserService userService;

    public AdminUserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public UserResponse createUser(@Valid @RequestBody UserRequest request) {
        return userService.createUser(request);
    }

    @GetMapping
    public List<UserResponse> getAllUsers() {
        return userService.getAllUsers();
    }

    @PutMapping("/{id}")
    public UserResponse updateUser(@PathVariable Long id,
                                   @Valid @RequestBody UserRequest request) {
        return userService.updateUser(id, request);
    }

    @PutMapping("/{id}/verify")
    public UserResponse verifyUser(@PathVariable Long id) {
        return userService.updateVerificationStatus(id, VerificationStatus.APPROVED);
    }

    @PutMapping("/{id}/status")
    public UserResponse updateVerificationStatus(@PathVariable Long id,
                                                 @RequestParam VerificationStatus status) {
        return userService.updateVerificationStatus(id, status);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }
}
