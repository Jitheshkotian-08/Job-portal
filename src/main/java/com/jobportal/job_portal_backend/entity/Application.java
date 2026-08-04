package com.jobportal.job_portal_backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "applications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id", nullable = false)
    private Job job;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "candidate_id", nullable = false)
    private User candidate;

    @Column(name = "resume_path")
    private String resumePath;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ApplicationStatus status = ApplicationStatus.APPLIED;

    @Column(name = "applied_date")
    private LocalDateTime appliedDate;

    @PrePersist
    protected void onCreate() {
        this.appliedDate = LocalDateTime.now();
    }
}