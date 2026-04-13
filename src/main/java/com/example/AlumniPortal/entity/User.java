package com.example.AlumniPortal.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    private String password;

    private String displayName;

    private String authProvider = "LOCAL";

    private boolean profileCompleted;

    // ADMIN / ALUMNI
    private String role;

    // must be true to allow login
    private boolean verified;

    @Enumerated(EnumType.STRING)
    @Column(name = "verification_status")
    private VerificationStatus verificationStatus = VerificationStatus.PENDING;

    public VerificationStatus getEffectiveVerificationStatus() {
        if (verificationStatus != null) {
            return verificationStatus;
        }
        return verified ? VerificationStatus.APPROVED : VerificationStatus.PENDING;
    }

    public void setVerificationStatus(VerificationStatus verificationStatus) {
        this.verificationStatus = verificationStatus;
        this.verified = verificationStatus == VerificationStatus.APPROVED;
    }

    @PrePersist
    @PreUpdate
    public void syncVerificationState() {
        if (verificationStatus == null) {
            verificationStatus = verified
                    ? VerificationStatus.APPROVED
                    : VerificationStatus.PENDING;
        }
        verified = verificationStatus == VerificationStatus.APPROVED;
    }
}
