package com.jobportal.job_portal_backend.service.impl;

import com.jobportal.job_portal_backend.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);

    private final JavaMailSender mailSender;

    @Override
    public void sendApplicationStatusEmail(String toEmail, String candidateName, String jobTitle, String status) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            message.setSubject("Application Update: " + jobTitle);
            message.setText(buildEmailBody(candidateName, jobTitle, status));
            mailSender.send(message);
        } catch (Exception e) {
            logger.error("Failed to send email to {}: {}", toEmail, e.getMessage());
        }
    }

    private String buildEmailBody(String candidateName, String jobTitle, String status) {
        return String.format(
                "Hi %s,%n%nYour application for the position \"%s\" has been updated to: %s.%n%n" +
                        "Thank you for applying through Job Portal.%n%nBest regards,%nJob Portal Team",
                candidateName, jobTitle, status
        );
    }
}