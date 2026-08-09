package com.fundbridge.user.client;

import com.fundbridge.common.dto.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "auth-service")
public interface AuthClient {

    @PutMapping("/auth/internal/{id}/status")
    ResponseEntity<ApiResponse<Void>> updateStatus(@PathVariable("id") Long id, @RequestParam("active") boolean active);
}
