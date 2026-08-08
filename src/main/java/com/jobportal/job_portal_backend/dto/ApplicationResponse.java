package com.jobportal.job_portal_backend.dto;

import com.jobportal.job_portal_backend.entity.ApplicationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class ApplicationResponse {
    private Long id;
    private Long jobId;
    private String jobTitle;
    private Long candidateId;
    private String candidateName;
    private String candidateEmail;
    private String resumePath;
    private ApplicationStatus status;
    private LocalDateTime appliedDate;
}