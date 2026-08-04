package com.jobportal.job_portal_backend.service.impl;

import com.jobportal.job_portal_backend.dto.AuthResponse;
import com.jobportal.job_portal_backend.dto.LoginRequest;
import com.jobportal.job_portal_backend.dto.RegisterRequest;
import com.jobportal.job_portal_backend.entity.User;
import com.jobportal.job_portal_backend.exception.EmailAlreadyExistsException;
import com.jobportal.job_portal_backend.repository.UserRepository;
import com.jobportal.job_portal_backend.security.JwtUtil;
import com.jobportal.job_portal_backend.security.UserPrincipal;
import com.jobportal.job_portal_backend.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    @Override
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException("Email already registered: " + request.getEmail());
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .role(request.getRole())
                .build();

        User savedUser = userRepository.save(user);
        UserPrincipal userPrincipal = new UserPrincipal(savedUser);
        String token = jwtUtil.generateToken(userPrincipal);

        return AuthResponse.builder()
                .id(savedUser.getId())
                .name(savedUser.getName())
                .email(savedUser.getEmail())
                .role(savedUser.getRole())
                .token(token)
                .build();
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
        } catch (Exception e) {
            throw new BadCredentialsException("Invalid email or password");
        }

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

        UserPrincipal userPrincipal = new UserPrincipal(user);
        String token = jwtUtil.generateToken(userPrincipal);

        return AuthResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .token(token)
                .build();
    }
}