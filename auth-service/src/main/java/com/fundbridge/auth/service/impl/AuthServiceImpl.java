package com.fundbridge.auth.service.impl;

import com.fundbridge.auth.entity.AuthUser;
import com.fundbridge.auth.repository.AuthUserRepository;
import com.fundbridge.auth.service.AuthService;
import com.fundbridge.common.dto.request.LoginRequest;
import com.fundbridge.common.dto.request.RegisterRequest;
import com.fundbridge.common.dto.response.AuthResponse;
import com.fundbridge.common.enums.Role;
import com.fundbridge.common.exception.BadRequestException;
import com.fundbridge.common.security.JwtUtil;
import com.fundbridge.auth.client.UserClient;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final AuthUserRepository authUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserClient userClient;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (authUserRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already registered: " + request.getEmail());
        }
        if (authUserRepository.existsByUsername(request.getUsername())) {
            throw new BadRequestException("Username already taken: " + request.getUsername());
        }

        AuthUser user = AuthUser.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .phoneNumber(request.getPhoneNumber())
                .address(request.getAddress())
                .role(Role.ROLE_USER)
                .isActive(true)
                .build();

        authUserRepository.save(user);

        // Sync to user-service
        try {
            Map<String, Object> profileRequest = new HashMap<>();
            profileRequest.put("authUserId", user.getId());
            profileRequest.put("username", user.getUsername());
            profileRequest.put("email", user.getEmail());
            profileRequest.put("fullName", user.getFullName());
            profileRequest.put("phoneNumber", user.getPhoneNumber());
            profileRequest.put("address", user.getAddress());
            profileRequest.put("role", user.getRole().name());
            userClient.createUserProfile(profileRequest);
        } catch (Exception e) {
            log.error("Failed to sync user profile to user-service", e);
        }

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name(), user.getId());

        return AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .userId(user.getId())
                .email(user.getEmail())
                .username(user.getUsername())
                .role(user.getRole().name())
                .build();
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        AuthUser user = authUserRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadRequestException("User not found"));

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name(), user.getId());

        return AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .userId(user.getId())
                .email(user.getEmail())
                .username(user.getUsername())
                .role(user.getRole().name())
                .build();
    }
}
