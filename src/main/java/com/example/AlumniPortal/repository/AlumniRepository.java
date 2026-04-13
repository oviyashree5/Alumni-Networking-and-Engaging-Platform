package com.example.AlumniPortal.repository;

import com.example.AlumniPortal.entity.Alumni;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AlumniRepository extends JpaRepository<Alumni, Long> {

    Optional<Alumni> findByEmailIgnoreCase(String email);
}
