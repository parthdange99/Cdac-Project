package com.fundbridge.user.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateProfileRequest {

    @Size(max = 100, message = "Full name must be under 100 characters")
    private String fullName;

    @Size(max = 15, message = "Phone number must be under 15 characters")
    private String phoneNumber;

    @Size(max = 500, message = "Address must be under 500 characters")
    private String address;
}
