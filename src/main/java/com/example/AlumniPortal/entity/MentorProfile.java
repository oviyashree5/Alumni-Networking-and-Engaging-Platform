package com.example.AlumniPortal.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MentorProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String company;
    private String designation;
    private String location;
    private String domain;
    private Integer yearsExperience;
    private String photoUrl;

    @Column(length = 2000)
    private String bio;

    @ElementCollection
    @CollectionTable(name = "mentor_expertise",
            joinColumns = @JoinColumn(name = "mentor_profile_id"))
    @Column(name = "expertise")
    private List<String> expertiseAreas;

    private Double ratingAverage;
    private Integer totalReviews;
    private Integer sessionsCompleted;
    private boolean active;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @OneToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;
}
