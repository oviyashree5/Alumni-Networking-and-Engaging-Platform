package com.example.AlumniPortal.service;

import com.example.AlumniPortal.dto.UserRequest;
import com.example.AlumniPortal.dto.UserResponse;
import com.example.AlumniPortal.entity.User;
import com.example.AlumniPortal.entity.VerificationStatus;
import com.example.AlumniPortal.exception.BadRequestException;
import com.example.AlumniPortal.exception.ResourceNotFoundException;
import com.example.AlumniPortal.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepo;
    private final PasswordEncoder encoder;

    public UserService(UserRepository userRepo,
                       PasswordEncoder encoder) {
        this.userRepo = userRepo;
        this.encoder = encoder;
    }

    public UserResponse createUser(UserRequest request) {
        if (userRepo.findByEmailIgnoreCase(request.getEmail()).isPresent()) {
            throw new BadRequestException("User already exists");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setDisplayName(request.getEmail());
        user.setRole("ALUMNI");
        user.setPassword(encoder.encode(request.getPassword()));
        user.setAuthProvider("LOCAL");
        user.setProfileCompleted(false);
        user.setVerificationStatus(VerificationStatus.APPROVED);

        return mapToResponse(userRepo.save(user));
    }

    public List<UserResponse> getAllUsers() {
        return userRepo.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public UserResponse updateUser(Long id, UserRequest request) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        user.setEmail(request.getEmail());
        user.setPassword(encoder.encode(request.getPassword()));

        return mapToResponse(userRepo.save(user));
    }

    public UserResponse updateVerificationStatus(Long id, VerificationStatus status) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        user.setVerificationStatus(status);
        return mapToResponse(userRepo.save(user));
    }

    public void deleteUser(Long id) {
        userRepo.deleteById(id);
    }

    private UserResponse mapToResponse(User user) {
        VerificationStatus status = user.getEffectiveVerificationStatus();
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getRole(),
                status == VerificationStatus.APPROVED,
                status
        );
    }
}
