package com.example.AlumniPortal.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MentorshipRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "mentor_id")
    private User mentor;

    @ManyToOne
    @JoinColumn(name = "mentee_id")
    private User mentee;

    @ManyToOne
    @JoinColumn(name = "availability_slot_id")
    private MentorAvailability availabilitySlot;

    private String areaOfInterest;

    @Column(length = 2000)
    private String message;

    private LocalDate requestedDate;
    private Integer durationMinutes;
    private String sessionMode;

    @Column(length = 3000)
    private String mentorResponse;

    private String status; // PENDING, ACCEPTED, REJECTED, COMPLETED

    private LocalDateTime createdAt;
    private LocalDateTime respondedAt;
}
