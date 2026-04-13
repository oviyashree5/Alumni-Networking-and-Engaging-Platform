package com.example.AlumniPortal.repository;

import com.example.AlumniPortal.entity.MentorAvailability;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MentorAvailabilityRepository extends JpaRepository<MentorAvailability, Long> {

    List<MentorAvailability> findByMentorProfileUserIdAndActiveTrueOrderByDayOfWeekAscStartTimeAsc(Long userId);
}
