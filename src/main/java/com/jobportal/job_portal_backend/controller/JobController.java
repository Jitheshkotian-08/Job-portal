package com.jobportal.job_portal_backend.controller;

import com.jobportal.job_portal_backend.dto.JobRequest;
import com.jobportal.job_portal_backend.dto.JobResponse;
import com.jobportal.job_portal_backend.entity.JobType;
import com.jobportal.job_portal_backend.security.SecurityUtils;
import com.jobportal.job_portal_backend.service.JobService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class JobController {

    private final JobService jobService;

    // ---------- Recruiter endpoints ----------

    @PostMapping("/api/recruiter/jobs")
    public ResponseEntity<JobResponse> createJob(@Valid @RequestBody JobRequest request) {
        Long recruiterId = SecurityUtils.getCurrentUserId();
        JobResponse response = jobService.createJob(request, recruiterId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/api/recruiter/jobs/{id}")
    public ResponseEntity<JobResponse> updateJob(@PathVariable Long id, @Valid @RequestBody JobRequest request) {
        Long recruiterId = SecurityUtils.getCurrentUserId();
        JobResponse response = jobService.updateJob(id, request, recruiterId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/api/recruiter/jobs/{id}")
    public ResponseEntity<Void> deleteJob(@PathVariable Long id) {
        Long recruiterId = SecurityUtils.getCurrentUserId();
        jobService.deleteJob(id, recruiterId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/api/recruiter/jobs")
    public ResponseEntity<List<JobResponse>> getMyJobs() {
        Long recruiterId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(jobService.getJobsByRecruiter(recruiterId));
    }

    // ---------- Public endpoints ----------

    @GetMapping("/api/jobs/search")
    public ResponseEntity<Page<JobResponse>> searchJobs(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) JobType jobType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "postedDate") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {

        Sort sort = direction.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<JobResponse> results = jobService.searchJobs(keyword, location, jobType, pageable);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/api/jobs/{id}")
    public ResponseEntity<JobResponse> getJobById(@PathVariable Long id) {
        return ResponseEntity.ok(jobService.getJobById(id));
    }
}