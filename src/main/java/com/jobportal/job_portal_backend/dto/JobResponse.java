package com.jobportal.job_portal_backend.dto;

import com.jobportal.job_portal_backend.entity.JobStatus;
import com.jobportal.job_portal_backend.entity.JobType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class JobResponse {
    private Long id;
    private String title;
    private String description;
    private String skillsRequired;
    private String location;
    private Double salary;
    private JobType jobType;
    private JobStatus status;
    private Long postedById;
    private String postedByName;
    private LocalDateTime postedDate;
    private LocalDate applicationDeadline;
}