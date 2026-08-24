package com.jobportal.job_portal_backend.service;

public interface EmailService {
    void sendApplicationStatusEmail(String toEmail, String candidateName, String jobTitle, String status);
}