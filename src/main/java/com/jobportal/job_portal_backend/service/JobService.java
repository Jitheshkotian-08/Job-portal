package com.jobportal.job_portal_backend.service;

import com.jobportal.job_portal_backend.dto.JobRequest;
import com.jobportal.job_portal_backend.dto.JobResponse;
import com.jobportal.job_portal_backend.entity.JobType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface JobService {
    JobResponse createJob(JobRequest request, Long recruiterId);
    JobResponse updateJob(Long jobId, JobRequest request, Long recruiterId);
    void deleteJob(Long jobId, Long recruiterId);
    JobResponse getJobById(Long jobId);
    List<JobResponse> getJobsByRecruiter(Long recruiterId);
    Page<JobResponse> searchJobs(String keyword, String location, JobType jobType, Pageable pageable);
}