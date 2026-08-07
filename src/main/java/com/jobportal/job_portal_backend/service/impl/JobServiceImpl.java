package com.jobportal.job_portal_backend.service.impl;

import com.jobportal.job_portal_backend.dto.JobRequest;
import com.jobportal.job_portal_backend.dto.JobResponse;
import com.jobportal.job_portal_backend.entity.Job;
import com.jobportal.job_portal_backend.entity.JobStatus;
import com.jobportal.job_portal_backend.entity.JobType;
import com.jobportal.job_portal_backend.entity.User;
import com.jobportal.job_portal_backend.exception.ResourceNotFoundException;
import com.jobportal.job_portal_backend.exception.UnauthorizedActionException;
import com.jobportal.job_portal_backend.repository.JobRepository;
import com.jobportal.job_portal_backend.repository.UserRepository;
import com.jobportal.job_portal_backend.repository.specification.JobSpecification;
import com.jobportal.job_portal_backend.service.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class JobServiceImpl implements JobService {

    private final JobRepository jobRepository;
    private final UserRepository userRepository;

    @Override
    public JobResponse createJob(JobRequest request, Long recruiterId) {
        User recruiter = userRepository.findById(recruiterId)
                .orElseThrow(() -> new ResourceNotFoundException("Recruiter not found"));

        Job job = Job.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .skillsRequired(request.getSkillsRequired())
                .location(request.getLocation())
                .salary(request.getSalary())
                .jobType(request.getJobType())
                .applicationDeadline(request.getApplicationDeadline())
                .postedBy(recruiter)
                .status(JobStatus.ACTIVE)
                .build();

        Job saved = jobRepository.save(job);
        return mapToResponse(saved);
    }

    @Override
    public JobResponse updateJob(Long jobId, JobRequest request, Long recruiterId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + jobId));

        if (!job.getPostedBy().getId().equals(recruiterId)) {
            throw new UnauthorizedActionException("You are not authorized to update this job");
        }

        job.setTitle(request.getTitle());
        job.setDescription(request.getDescription());
        job.setSkillsRequired(request.getSkillsRequired());
        job.setLocation(request.getLocation());
        job.setSalary(request.getSalary());
        job.setJobType(request.getJobType());
        job.setApplicationDeadline(request.getApplicationDeadline());

        Job updated = jobRepository.save(job);
        return mapToResponse(updated);
    }

    @Override
    public void deleteJob(Long jobId, Long recruiterId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + jobId));

        if (!job.getPostedBy().getId().equals(recruiterId)) {
            throw new UnauthorizedActionException("You are not authorized to delete this job");
        }

        jobRepository.delete(job);
    }

    @Override
    public JobResponse getJobById(Long jobId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + jobId));
        return mapToResponse(job);
    }

    @Override
    public List<JobResponse> getJobsByRecruiter(Long recruiterId) {
        return jobRepository.findByPostedById(recruiterId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public Page<JobResponse> searchJobs(String keyword, String location, JobType jobType, Pageable pageable) {
        Specification<Job> spec = Specification
                .where(JobSpecification.hasKeyword(keyword))
                .and(JobSpecification.hasLocation(location))
                .and(JobSpecification.hasJobType(jobType))
                .and(JobSpecification.hasStatus(JobStatus.ACTIVE));

        return jobRepository.findAll(spec, pageable).map(this::mapToResponse);
    }

    private JobResponse mapToResponse(Job job) {
        return JobResponse.builder()
                .id(job.getId())
                .title(job.getTitle())
                .description(job.getDescription())
                .skillsRequired(job.getSkillsRequired())
                .location(job.getLocation())
                .salary(job.getSalary())
                .jobType(job.getJobType())
                .status(job.getStatus())
                .postedById(job.getPostedBy().getId())
                .postedByName(job.getPostedBy().getName())
                .postedDate(job.getPostedDate())
                .applicationDeadline(job.getApplicationDeadline())
                .build();
    }
}