package com.fundbridge.user.controller;

import com.fundbridge.common.dto.response.ApiResponse;
import com.fundbridge.user.dto.CreateUserRequest;
import com.fundbridge.user.dto.UpdateProfileRequest;
import com.fundbridge.user.dto.UserResponse;
import com.fundbridge.user.entity.UserProfile;
import com.fundbridge.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<UserResponse>> getMyProfile(
            @RequestHeader("X-User-Email") String email) {
        UserProfile user = userService.getProfileByEmail(email);
        return ResponseEntity.ok(ApiResponse.success(UserResponse.fromEntity(user)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable Long id) {
        UserProfile user = userService.getUserById(id);
        return ResponseEntity.ok(ApiResponse.success(UserResponse.fromEntity(user)));
    }

    @GetMapping("/auth/{authUserId}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserByAuthId(@PathVariable Long authUserId) {
        UserProfile user = userService.getUserByAuthUserId(authUserId);
        return ResponseEntity.ok(ApiResponse.success(UserResponse.fromEntity(user)));
    }

    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<UserResponse>> updateProfile(
            @Valid @RequestBody UpdateProfileRequest request,
            @RequestHeader("X-User-Email") String email) {
        UserProfile updated = userService.updateProfile(email, request);
        return ResponseEntity.ok(ApiResponse.success("Profile updated", UserResponse.fromEntity(updated)));
    }

    @PostMapping("/internal/create")
    public ResponseEntity<ApiResponse<UserResponse>> createUserProfile(
            @RequestBody CreateUserRequest request) {
        UserProfile created = userService.createUserProfile(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Profile created", UserResponse.fromEntity(created)));
    }

    @GetMapping("/admin/all")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers(
            @RequestHeader("X-User-Role") String role) {
        if (!role.equals("ROLE_ADMIN")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("Access denied"));
        }
        List<UserResponse> users = userService.getAllUsers().stream()
                .map(UserResponse::fromEntity)
                .toList();
        return ResponseEntity.ok(ApiResponse.success(users));
    }

    @PatchMapping("/admin/{id}/deactivate")
    public ResponseEntity<ApiResponse<Void>> deactivateUser(
            @PathVariable Long id,
            @RequestHeader("X-User-Role") String role) {
        if (!role.equals("ROLE_ADMIN")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("Access denied"));
        }
        userService.deactivateUser(id);
        return ResponseEntity.ok(ApiResponse.success("User deactivated", null));
    }

    @PatchMapping("/admin/{id}/activate")
    public ResponseEntity<ApiResponse<Void>> activateUser(
            @PathVariable Long id,
            @RequestHeader("X-User-Role") String role) {
        if (!role.equals("ROLE_ADMIN")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("Access denied"));
        }
        userService.activateUser(id);
        return ResponseEntity.ok(ApiResponse.success("User activated", null));
    }
}
