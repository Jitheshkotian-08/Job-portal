package com.jobportal.job_portal_backend.service.impl;

import com.jobportal.job_portal_backend.dto.ApplicationResponse;
import com.jobportal.job_portal_backend.entity.Application;
import com.jobportal.job_portal_backend.entity.ApplicationStatus;
import com.jobportal.job_portal_backend.entity.Job;
import com.jobportal.job_portal_backend.entity.JobStatus;
import com.jobportal.job_portal_backend.entity.User;
import com.jobportal.job_portal_backend.exception.InvalidOperationException;
import com.jobportal.job_portal_backend.exception.ResourceNotFoundException;
import com.jobportal.job_portal_backend.exception.UnauthorizedActionException;
import com.jobportal.job_portal_backend.repository.ApplicationRepository;
import com.jobportal.job_portal_backend.repository.JobRepository;
import com.jobportal.job_portal_backend.repository.UserRepository;
import com.jobportal.job_portal_backend.service.ApplicationService;
import com.jobportal.job_portal_backend.service.FileStorageService;
import com.jobportal.job_portal_backend.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ApplicationServiceImpl implements ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final JobRepository jobRepository;
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;
    private final EmailService emailService;

    @Override
    public ApplicationResponse applyToJob(Long jobId, Long candidateId, MultipartFile resume) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + jobId));

        if (job.getStatus() != JobStatus.ACTIVE) {
            throw new InvalidOperationException("This job is no longer accepting applications");
        }

        if (applicationRepository.existsByJobIdAndCandidateId(jobId, candidateId)) {
            throw new InvalidOperationException("You have already applied to this job");
        }

        User candidate = userRepository.findById(candidateId)
                .orElseThrow(() -> new ResourceNotFoundException("Candidate not found"));

        String storedFilename = fileStorageService.storeFile(resume);
        String resumePath = "/uploads/resumes/" + storedFilename;

        Application application = Application.builder()
                .job(job)
                .candidate(candidate)
                .resumePath(resumePath)
                .status(ApplicationStatus.APPLIED)
                .build();

        Application saved = applicationRepository.save(application);
        return mapToResponse(saved);
    }

    @Override
    public List<ApplicationResponse> getApplicationsByCandidate(Long candidateId) {
        return applicationRepository.findByCandidateId(candidateId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<ApplicationResponse> getApplicationsByJob(Long jobId, Long recruiterId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + jobId));

        if (!job.getPostedBy().getId().equals(recruiterId)) {
            throw new UnauthorizedActionException("You are not authorized to view applicants for this job");
        }

        return applicationRepository.findByJobId(jobId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public ApplicationResponse updateStatus(Long applicationId, Long recruiterId, ApplicationStatus status) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found with id: " + applicationId));

        if (!application.getJob().getPostedBy().getId().equals(recruiterId)) {
            throw new UnauthorizedActionException("You are not authorized to update this application");
        }

        application.setStatus(status);
        Application updated = applicationRepository.save(application);

        emailService.sendApplicationStatusEmail(
                updated.getCandidate().getEmail(),
                updated.getCandidate().getName(),
                updated.getJob().getTitle(),
                updated.getStatus().name()
        );

        return mapToResponse(updated);
    }

    private ApplicationResponse mapToResponse(Application application) {
        return ApplicationResponse.builder()
                .id(application.getId())
                .jobId(application.getJob().getId())
                .jobTitle(application.getJob().getTitle())
                .candidateId(application.getCandidate().getId())
                .candidateName(application.getCandidate().getName())
                .candidateEmail(application.getCandidate().getEmail())
                .resumePath(application.getResumePath())
                .status(application.getStatus())
                .appliedDate(application.getAppliedDate())
                .build();
    }
}