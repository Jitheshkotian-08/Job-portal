package com.jobportal.job_portal_backend.repository;

import com.jobportal.job_portal_backend.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface JobRepository extends JpaRepository<Job, Long>, JpaSpecificationExecutor<Job> {
    List<Job> findByPostedById(Long recruiterId);
}