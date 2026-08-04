package com.jobportal.job_portal_backend.service;

import com.jobportal.job_portal_backend.dto.AuthResponse;
import com.jobportal.job_portal_backend.dto.LoginRequest;
import com.jobportal.job_portal_backend.dto.RegisterRequest;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
}