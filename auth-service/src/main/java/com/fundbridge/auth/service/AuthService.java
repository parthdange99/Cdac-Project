package com.fundbridge.auth.service;

import com.fundbridge.common.dto.request.LoginRequest;
import com.fundbridge.common.dto.request.RegisterRequest;
import com.fundbridge.common.dto.response.AuthResponse;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
}
