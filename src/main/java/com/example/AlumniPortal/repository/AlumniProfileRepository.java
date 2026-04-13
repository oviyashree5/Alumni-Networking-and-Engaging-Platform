package com.example.AlumniPortal.repository;

import com.example.AlumniPortal.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
public interface AlumniProfileRepository extends JpaRepository<AlumniProfile, Long> {
    Optional<AlumniProfile> findByUserId(Long userId);

    void deleteByUserId(Long userId);
}
