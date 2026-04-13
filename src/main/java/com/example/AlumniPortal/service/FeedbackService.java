package com.example.AlumniPortal.service;

import com.example.AlumniPortal.dto.FeedbackRequest;
import com.example.AlumniPortal.dto.FeedbackResponse;
import com.example.AlumniPortal.entity.AlumniProfile;
import com.example.AlumniPortal.entity.Feedback;
import com.example.AlumniPortal.entity.User;
import com.example.AlumniPortal.repository.AlumniProfileRepository;
import com.example.AlumniPortal.repository.FeedbackRepository;
import com.example.AlumniPortal.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final UserRepository userRepository;
    private final AlumniProfileRepository alumniProfileRepository;

    public FeedbackResponse submitFeedback(Long userId, FeedbackRequest request) {
        Feedback feedback = Feedback.builder()
                .userId(userId)
                .message(request.getMessage().trim())
                .rating(request.getRating())
                .build();

        return mapToResponse(feedbackRepository.save(feedback));
    }

    public List<FeedbackResponse> getAllFeedback() {
        return feedbackRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private FeedbackResponse mapToResponse(Feedback feedback) {
        Optional<User> userOptional = userRepository.findById(feedback.getUserId());
        Optional<AlumniProfile> profileOptional = alumniProfileRepository.findByUserId(feedback.getUserId());

        String userEmail = userOptional.map(User::getEmail).orElse("Unknown user");
        String userName = profileOptional
                .map(AlumniProfile::getName)
                .filter(name -> name != null && !name.isBlank() && !"Not Updated Yet".equalsIgnoreCase(name))
                .orElse(userEmail);
        String batchYear = profileOptional.map(AlumniProfile::getBatchYear).orElse(null);
        String department = profileOptional.map(AlumniProfile::getDepartment).orElse(null);

        return new FeedbackResponse(
                feedback.getId(),
                feedback.getUserId(),
                userName,
                userEmail,
                batchYear,
                department,
                feedback.getMessage(),
                feedback.getRating(),
                feedback.getCreatedAt()
        );
    }
}
