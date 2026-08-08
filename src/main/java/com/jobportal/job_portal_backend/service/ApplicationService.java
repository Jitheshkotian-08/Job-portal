package com.jobportal.job_portal_backend.service;

import com.jobportal.job_portal_backend.dto.ApplicationResponse;
import com.jobportal.job_portal_backend.entity.ApplicationStatus;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ApplicationService {
    ApplicationResponse applyToJob(Long jobId, Long candidateId, MultipartFile resume);
    List<ApplicationResponse> getApplicationsByCandidate(Long candidateId);
    List<ApplicationResponse> getApplicationsByJob(Long jobId, Long recruiterId);
    ApplicationResponse updateStatus(Long applicationId, Long recruiterId, ApplicationStatus status);
}