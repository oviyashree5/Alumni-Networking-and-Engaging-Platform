package com.example.AlumniPortal.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "alumni")
@Getter
@Setter
public class Alumni {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String name;

    private String batchYear;

    private String department;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AlumniRegistrationStatus registrationStatus = AlumniRegistrationStatus.NOT_REGISTERED;

    @Column(unique = true)
    private Long userId;

    @Column(nullable = false)
    private LocalDateTime importedAt = LocalDateTime.now();

    private LocalDateTime registeredAt;
}
