package com.example.AlumniPortal.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Entity
@Table(name = "alumni_profiles")
@Getter
@Setter
public class AlumniProfile implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long userId;

    @Column(nullable = false)
    private String name;          // Full name of alumni

    private String batchYear;     // Graduation year
    private String department;
    private String skills;
    private String profession;    // Current job / role
    private String location;      // City / Country
    private String contact;       // Personal email / phone
    private String linkedinUrl;
    @Lob
    @Column(name = "profile_photo", columnDefinition = "LONGTEXT")
    private String profilePhoto;
}
