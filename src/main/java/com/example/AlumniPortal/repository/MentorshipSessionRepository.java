package com.example.AlumniPortal.repository;

import com.example.AlumniPortal.entity.MentorshipSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MentorshipSessionRepository extends JpaRepository<MentorshipSession, Long> {

    List<MentorshipSession> findByMenteeIdOrMentorIdOrderByScheduledAtDesc(Long menteeId, Long mentorId);

    Optional<MentorshipSession> findByRequestId(Long requestId);

    long countByMentorIdAndStatus(Long mentorId, String status);
}
