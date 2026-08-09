package com.fundbridge.user.service.impl;

import com.fundbridge.common.enums.Role;
import com.fundbridge.common.exception.ResourceNotFoundException;
import com.fundbridge.user.dto.CreateUserRequest;
import com.fundbridge.user.dto.UpdateProfileRequest;
import com.fundbridge.user.entity.UserProfile;
import com.fundbridge.user.repository.UserProfileRepository;
import com.fundbridge.user.service.UserService;
import com.fundbridge.user.client.AuthClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserProfileRepository userProfileRepository;
    private final AuthClient authClient;

    @Override
    public UserProfile getProfileByEmail(String email) {
        return userProfileRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }

    @Override
    public UserProfile getUserById(Long id) {
        return userProfileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
    }

    @Override
    public UserProfile getUserByAuthUserId(Long authUserId) {
        return userProfileRepository.findByAuthUserId(authUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User profile not found for authUserId: " + authUserId));
    }

    @Override
    @Transactional
    public UserProfile updateProfile(String email, UpdateProfileRequest request) {
        UserProfile user = getProfileByEmail(email);
        if (request.getFullName() != null) {
            user.setFullName(request.getFullName());
        }
        if (request.getPhoneNumber() != null) {
            user.setPhoneNumber(request.getPhoneNumber());
        }
        if (request.getAddress() != null) {
            user.setAddress(request.getAddress());
        }
        return userProfileRepository.save(user);
    }

    @Override
    @Transactional
    public UserProfile createUserProfile(CreateUserRequest request) {
        UserProfile profile = UserProfile.builder()
                .authUserId(request.getAuthUserId())
                .username(request.getUsername())
                .email(request.getEmail())
                .fullName(request.getFullName())
                .phoneNumber(request.getPhoneNumber())
                .address(request.getAddress())
                .role(request.getRole() != null ? Role.valueOf(request.getRole()) : Role.ROLE_USER)
                .isActive(true)
                .build();
        return userProfileRepository.save(profile);
    }

    @Override
    public List<UserProfile> getAllUsers() {
        return userProfileRepository.findAll();
    }

    @Override
    @Transactional
    public void deactivateUser(Long id) {
        UserProfile user = getUserById(id);
        user.setActive(false);
        userProfileRepository.save(user);
        
        try {
            authClient.updateStatus(user.getAuthUserId(), false);
        } catch (Exception e) {
            log.error("Failed to sync deactivation with auth-service for authUserId: {}", user.getAuthUserId(), e);
            throw new RuntimeException("Failed to sync with auth-service: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public void activateUser(Long id) {
        UserProfile user = getUserById(id);
        user.setActive(true);
        userProfileRepository.save(user);
        
        try {
            authClient.updateStatus(user.getAuthUserId(), true);
        } catch (Exception e) {
            log.error("Failed to sync activation with auth-service for authUserId: {}", user.getAuthUserId(), e);
        }
    }
}
