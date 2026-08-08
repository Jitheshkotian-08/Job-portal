package com.jobportal.job_portal_backend.controller;

import com.jobportal.job_portal_backend.dto.ApplicationResponse;
import com.jobportal.job_portal_backend.dto.ApplicationStatusUpdateRequest;
import com.jobportal.job_portal_backend.security.SecurityUtils;
import com.jobportal.job_portal_backend.service.ApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;

    // ---------- Candidate endpoints ----------

    @PostMapping(value = "/api/candidate/applications/{jobId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApplicationResponse> apply(
            @PathVariable Long jobId,
            @RequestParam("resume") MultipartFile resume) {

        Long candidateId = SecurityUtils.getCurrentUserId();
        ApplicationResponse response = applicationService.applyToJob(jobId, candidateId, resume);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/api/candidate/applications")
    public ResponseEntity<List<ApplicationResponse>> myApplications() {
        Long candidateId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(applicationService.getApplicationsByCandidate(candidateId));
    }

    // ---------- Recruiter endpoints ----------

    @GetMapping("/api/recruiter/jobs/{jobId}/applications")
    public ResponseEntity<List<ApplicationResponse>> jobApplicants(@PathVariable Long jobId) {
        Long recruiterId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(applicationService.getApplicationsByJob(jobId, recruiterId));
    }

    @PutMapping("/api/recruiter/applications/{id}/status")
    public ResponseEntity<ApplicationResponse> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody ApplicationStatusUpdateRequest request) {

        Long recruiterId = SecurityUtils.getCurrentUserId();
        ApplicationResponse response = applicationService.updateStatus(id, recruiterId, request.getStatus());
        return ResponseEntity.ok(response);
    }
}