package com.fundbridge.user.dto;

import lombok.Data;

@Data
public class CreateUserRequest {
    private Long authUserId;
    private String username;
    private String email;
    private String fullName;
    private String phoneNumber;
    private String address;
    private String role;
}
