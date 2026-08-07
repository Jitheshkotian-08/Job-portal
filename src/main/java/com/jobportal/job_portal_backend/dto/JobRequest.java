package com.jobportal.job_portal_backend.dto;

import com.jobportal.job_portal_backend.entity.JobType;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class JobRequest {

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Description is required")
    private String description;

    private String skillsRequired;

    @NotBlank(message = "Location is required")
    private String location;

    @Positive(message = "Salary must be positive")
    private Double salary;

    @NotNull(message = "Job type is required")
    private JobType jobType;

    @NotNull(message = "Application deadline is required")
    @FutureOrPresent(message = "Deadline cannot be in the past")
    private LocalDate applicationDeadline;
}