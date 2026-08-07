package com.jobportal.job_portal_backend.repository.specification;

import com.jobportal.job_portal_backend.entity.Job;
import com.jobportal.job_portal_backend.entity.JobStatus;
import com.jobportal.job_portal_backend.entity.JobType;
import org.springframework.data.jpa.domain.Specification;

public class JobSpecification {

    public static Specification<Job> hasKeyword(String keyword) {
        return (root, query, cb) -> {
            if (keyword == null || keyword.isBlank()) return cb.conjunction();
            String pattern = "%" + keyword.toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("title")), pattern),
                    cb.like(cb.lower(root.get("skillsRequired")), pattern),
                    cb.like(cb.lower(root.get("description")), pattern)
            );
        };
    }

    public static Specification<Job> hasLocation(String location) {
        return (root, query, cb) -> {
            if (location == null || location.isBlank()) return cb.conjunction();
            return cb.like(cb.lower(root.get("location")), "%" + location.toLowerCase() + "%");
        };
    }

    public static Specification<Job> hasJobType(JobType jobType) {
        return (root, query, cb) -> {
            if (jobType == null) return cb.conjunction();
            return cb.equal(root.get("jobType"), jobType);
        };
    }

    public static Specification<Job> hasStatus(JobStatus status) {
        return (root, query, cb) -> {
            if (status == null) return cb.conjunction();
            return cb.equal(root.get("status"), status);
        };
    }
}