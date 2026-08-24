package com.jobportal.job_portal_backend.controller;

import com.jobportal.job_portal_backend.dto.UserProfileResponse;
import com.jobportal.job_portal_backend.dto.UserProfileUpdateRequest;
import com.jobportal.job_portal_backend.security.SecurityUtils;
import com.jobportal.job_portal_backend.service.ProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping
    public UserProfileResponse getProfile() {
        Long userId = SecurityUtils.getCurrentUserId();
        return profileService.getProfile(userId);
    }

    @PutMapping
    public UserProfileResponse updateProfile(@Valid @RequestBody UserProfileUpdateRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        return profileService.updateProfile(userId, request);
    }
}