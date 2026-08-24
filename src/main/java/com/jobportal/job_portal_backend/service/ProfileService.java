package com.jobportal.job_portal_backend.service;

import com.jobportal.job_portal_backend.dto.UserProfileResponse;
import com.jobportal.job_portal_backend.dto.UserProfileUpdateRequest;

public interface ProfileService {
    UserProfileResponse getProfile(Long userId);
    UserProfileResponse updateProfile(Long userId, UserProfileUpdateRequest request);
}