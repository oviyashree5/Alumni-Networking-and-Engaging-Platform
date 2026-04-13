package com.example.AlumniPortal.repository;

import com.example.AlumniPortal.entity.MentorProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface MentorProfileRepository extends JpaRepository<MentorProfile, Long> {

    List<MentorProfile> findByActiveTrue();
    @Query("SELECT m FROM MentorProfile m LEFT JOIN FETCH m.expertiseAreas")
    List<MentorProfile> findAllWithExpertise();

    List<MentorProfile> findTop5ByActiveTrueOrderByRatingAverageDescTotalReviewsDescSessionsCompletedDesc();

    List<MentorProfile> findTop5ByActiveTrueOrderByCreatedAtDesc();

    Optional<MentorProfile> findByUserId(Long userId);
}
