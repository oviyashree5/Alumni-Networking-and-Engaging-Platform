package com.example.AlumniPortal.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "events")
@Getter
@Setter
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String description;

    @Column(nullable = false)
    private String location;

    @Column(nullable = false)
    private LocalDateTime eventDate;
    private Integer maxSeats;

    private Integer registeredCount = 0;

    @Column(nullable = false)
    private String audienceType = "ALL";

    private String targetBatchYear;

    private LocalDateTime createdAt = LocalDateTime.now();
}
