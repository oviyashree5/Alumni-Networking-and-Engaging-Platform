package com.example.AlumniPortal.repository;

import com.example.AlumniPortal.entity.MentorshipRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MentorshipRequestRepository extends JpaRepository<MentorshipRequest, Long> {

    List<MentorshipRequest> findByMenteeId(Long menteeId);

    List<MentorshipRequest> findByMentorId(Long mentorId);

    List<MentorshipRequest> findByMenteeIdAndMentorId(Long menteeId, Long mentorId);
}
