package com.fundbridge.user.service;

import com.fundbridge.user.dto.CreateUserRequest;
import com.fundbridge.user.dto.UpdateProfileRequest;
import com.fundbridge.user.entity.UserProfile;

import java.util.List;

public interface UserService {
    UserProfile getProfileByEmail(String email);
    UserProfile getUserById(Long id);
    UserProfile getUserByAuthUserId(Long authUserId);
    UserProfile updateProfile(String email, UpdateProfileRequest request);
    UserProfile createUserProfile(CreateUserRequest request);
    List<UserProfile> getAllUsers();
    void deactivateUser(Long id);
}
