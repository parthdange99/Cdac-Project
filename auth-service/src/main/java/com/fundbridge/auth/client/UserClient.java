package com.fundbridge.auth.client;

import com.fundbridge.common.dto.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(name = "user-service")
public interface UserClient {

    @PostMapping("/users/internal/create")
    ResponseEntity<ApiResponse<Map<String, Object>>> createUserProfile(@RequestBody Map<String, Object> request);
}
