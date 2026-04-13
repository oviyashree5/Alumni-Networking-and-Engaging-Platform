package com.example.AlumniPortal.repository;

import com.example.AlumniPortal.entity.Announcement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {

    List<Announcement> findByCategoryIgnoreCase(String category);

    List<Announcement> findByPriorityIgnoreCase(String priority);

    List<Announcement> findByCategoryAndPriority(String category, String priority);
}
