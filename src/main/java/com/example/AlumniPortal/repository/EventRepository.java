package com.example.AlumniPortal.repository;

import com.example.AlumniPortal.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findByAudienceTypeOrTargetBatchYearOrderByEventDateAsc(String audienceType, String targetBatchYear);
}
