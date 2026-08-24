package com.jobportal.job_portal_backend.service.impl;

import com.jobportal.job_portal_backend.dto.UserProfileResponse;
import com.jobportal.job_portal_backend.dto.UserProfileUpdateRequest;
import com.jobportal.job_portal_backend.entity.User;
import com.jobportal.job_portal_backend.exception.ResourceNotFoundException;
import com.jobportal.job_portal_backend.repository.UserRepository;
import com.jobportal.job_portal_backend.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final UserRepository userRepository;

    @Override
    public UserProfileResponse getProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return mapToResponse(user);
    }

    @Override
    public UserProfileResponse updateProfile(Long userId, UserProfileUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        user.setName(request.getName());
        user.setPhone(request.getPhone());

        User updated = userRepository.save(user);
        return mapToResponse(updated);
    }

    private UserProfileResponse mapToResponse(User user) {
        return UserProfileResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .build();
    }
}