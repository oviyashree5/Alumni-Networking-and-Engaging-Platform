package com.example.AlumniPortal.repository;

import com.example.AlumniPortal.entity.MentorFeedback;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MentorFeedbackRepository extends JpaRepository<MentorFeedback, Long> {

    Optional<MentorFeedback> findBySessionId(Long sessionId);

    List<MentorFeedback> findByMentorId(Long mentorId);
}
