package com.example.AlumniPortal.service;

import com.example.AlumniPortal.dto.LoginRequest;
import com.example.AlumniPortal.dto.LoginResponse;
import com.example.AlumniPortal.entity.Alumni;
import com.example.AlumniPortal.entity.AlumniProfile;
import com.example.AlumniPortal.entity.User;
import com.example.AlumniPortal.entity.VerificationStatus;
import com.example.AlumniPortal.exception.BadRequestException;
import com.example.AlumniPortal.exception.ResourceNotFoundException;
import com.example.AlumniPortal.repository.AlumniProfileRepository;
import com.example.AlumniPortal.repository.UserRepository;
import com.example.AlumniPortal.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepo;
    private final AlumniRegistryService alumniRegistryService;
    private final AlumniProfileRepository alumniProfileRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder encoder;

    public LoginResponse login(LoginRequest request) {
        User user = userRepo.findByEmailIgnoreCase(normalizeEmail(request.getEmail()))
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (user.getEffectiveVerificationStatus() != VerificationStatus.APPROVED) {
            throw new BadRequestException("User is awaiting admin approval");
        }

        if (user.getPassword() == null || !encoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadRequestException("Invalid credentials");
        }

        return buildLoginResponse(user, false);
    }

    public LoginResponse processGoogleLogin(String email, String name) {
        String normalizedEmail = normalizeEmail(email);
        User existingUser = userRepo.findByEmailIgnoreCase(normalizedEmail).orElse(null);

        if (existingUser != null && "ADMIN".equalsIgnoreCase(existingUser.getRole())) {
            existingUser.setAuthProvider("GOOGLE");
            if (name != null && !name.isBlank()) {
                existingUser.setDisplayName(name.trim());
            }
            return buildLoginResponse(userRepo.save(existingUser), false);
        }

        Alumni alumni = alumniRegistryService.findByEmail(normalizedEmail)
                .orElseThrow(() -> new BadRequestException("Your email is not present in the alumni registry. Contact admin."));

        boolean firstTimeLogin = false;
        User user = existingUser;

        if (user == null) {
            user = new User();
            user.setEmail(normalizedEmail);
            user.setDisplayName(resolveDisplayName(alumni, name));
            user.setAuthProvider("GOOGLE");
            user.setRole("ALUMNI");
            user.setVerificationStatus(VerificationStatus.APPROVED);
            user.setProfileCompleted(false);
            user = userRepo.save(user);
            firstTimeLogin = true;
        } else {
            user.setAuthProvider("GOOGLE");
            if (user.getDisplayName() == null || user.getDisplayName().isBlank()) {
                user.setDisplayName(resolveDisplayName(alumni, name));
            }
            user = userRepo.save(user);
        }

        seedProfile(user.getId(), alumni, name);
        alumniRegistryService.markRegistered(alumni, user.getId());

        return buildLoginResponse(user, firstTimeLogin);
    }

    private void seedProfile(Long userId, Alumni alumni, String googleName) {
        AlumniProfile profile = alumniProfileRepository.findByUserId(userId)
                .orElseGet(() -> {
                    AlumniProfile newProfile = new AlumniProfile();
                    newProfile.setUserId(userId);
                    return newProfile;
                });

        if (profile.getName() == null || profile.getName().isBlank() || "Not Updated Yet".equalsIgnoreCase(profile.getName())) {
            profile.setName(resolveDisplayName(alumni, googleName));
        }
        if (profile.getBatchYear() == null || profile.getBatchYear().isBlank()) {
            profile.setBatchYear(alumni.getBatchYear());
        }
        if (profile.getDepartment() == null || profile.getDepartment().isBlank()) {
            profile.setDepartment(alumni.getDepartment());
        }

        alumniProfileRepository.save(profile);
    }

    private LoginResponse buildLoginResponse(User user, boolean firstTimeLogin) {
        String token = jwtUtil.generateToken(user.getEmail(), user.getRole());
        return new LoginResponse(
                token,
                user.getId(),
                user.getEmail(),
                user.getRole(),
                user.getEffectiveVerificationStatus(),
                user.isProfileCompleted(),
                firstTimeLogin
        );
    }

    private String resolveDisplayName(Alumni alumni, String googleName) {
        if (googleName != null && !googleName.isBlank()) {
            return googleName.trim();
        }
        if (alumni.getName() != null && !alumni.getName().isBlank()) {
            return alumni.getName().trim();
        }
        return alumni.getEmail();
    }

    private String normalizeEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new BadRequestException("Email is required");
        }
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
