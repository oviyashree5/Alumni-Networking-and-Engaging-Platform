package com.example.AlumniPortal.service;

import com.example.AlumniPortal.dto.AlumniDirectoryResponse;
import com.example.AlumniPortal.dto.AlumniProfileRequest;
import com.example.AlumniPortal.dto.AlumniProfileResponse;
import com.example.AlumniPortal.entity.AlumniProfile;
import com.example.AlumniPortal.entity.User;
import com.example.AlumniPortal.entity.VerificationStatus;
import com.example.AlumniPortal.repository.AlumniProfileRepository;
import com.example.AlumniPortal.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class AlumniProfileService {

    private final AlumniProfileRepository repository;
    private final UserRepository userRepository;

    public AlumniProfileService(AlumniProfileRepository repository,
                                UserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    public AlumniProfileResponse getProfileByUserId(Long userId) {
        AlumniProfile profile = repository.findByUserId(userId)
                .orElseGet(() -> {
                    AlumniProfile newProfile = new AlumniProfile();
                    newProfile.setUserId(userId);
                    newProfile.setName("Not Updated Yet");
                    return repository.save(newProfile);
                });

        return mapToResponse(profile);
    }

    public AlumniProfileResponse updateProfile(Long userId,
                                               AlumniProfileRequest request) {

        AlumniProfile profile = repository.findByUserId(userId)
                .orElseGet(() -> {
                    AlumniProfile newProfile = new AlumniProfile();
                    newProfile.setUserId(userId);
                    return newProfile;
                });

        profile.setName(request.getName());
        profile.setBatchYear(request.getBatchYear());
        profile.setDepartment(request.getDepartment());
        profile.setSkills(request.getSkills());
        profile.setProfession(request.getProfession());
        profile.setLocation(request.getLocation());
        profile.setContact(request.getContact());
        profile.setLinkedinUrl(normalizeLinkedinUrl(request.getLinkedinUrl()));
        profile.setProfilePhoto(request.getProfilePhoto());

        repository.save(profile);
        userRepository.findById(userId).ifPresent(user -> {
            user.setDisplayName(profile.getName());
            user.setProfileCompleted(true);
            userRepository.save(user);
        });

        return mapToResponse(profile);
    }

    public List<AlumniDirectoryResponse> getDirectory() {
        return userRepository.findByRole("ALUMNI")
                .stream()
                .filter(user -> user.getEffectiveVerificationStatus() == VerificationStatus.APPROVED)
                .map(this::mapToDirectoryResponse)
                .sorted(Comparator.comparing(
                        AlumniDirectoryResponse::getName,
                        Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)
                ))
                .toList();
    }

    private AlumniProfileResponse mapToResponse(AlumniProfile profile) {
        return new AlumniProfileResponse(
                profile.getUserId(),
                profile.getName(),
                profile.getBatchYear(),
                profile.getDepartment(),
                profile.getSkills(),
                profile.getProfession(),
                profile.getLocation(),
                profile.getContact(),
                profile.getLinkedinUrl(),
                profile.getProfilePhoto()

        );
    }

    private AlumniDirectoryResponse mapToDirectoryResponse(User user) {
        Optional<AlumniProfile> profileOptional = repository.findByUserId(user.getId());

        if (profileOptional.isEmpty()) {
            return new AlumniDirectoryResponse(
                    user.getId(),
                    user.getEmail(),
                    null,
                    null,
                    null,
                    null,
                    null,
                    null
            );
        }

        AlumniProfile profile = profileOptional.get();
        return new AlumniDirectoryResponse(
                user.getId(),
                profile.getName(),
                profile.getBatchYear(),
                profile.getDepartment(),
                profile.getSkills(),
                profile.getProfession(),
                profile.getLocation(),
                profile.getLinkedinUrl()
        );
    }

    private String normalizeLinkedinUrl(String linkedinUrl) {
        if (linkedinUrl == null || linkedinUrl.isBlank()) {
            return null;
        }

        String trimmed = linkedinUrl.trim();
        return trimmed.startsWith("http") ? trimmed : "https://" + trimmed;
    }
}
