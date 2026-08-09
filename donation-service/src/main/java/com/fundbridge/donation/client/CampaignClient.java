package com.fundbridge.donation.client;

import com.fundbridge.common.dto.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@FeignClient(name = "campaign-service")
public interface CampaignClient {

    @GetMapping("/campaigns/public/{id}")
    ResponseEntity<ApiResponse<Map<String, Object>>> getCampaignById(@PathVariable Long id);

    @PutMapping("/campaigns/internal/{id}/raised-amount")
    ResponseEntity<ApiResponse<Map<String, Object>>> updateRaisedAmount(
            @PathVariable Long id,
            @RequestBody Map<String, BigDecimal> request);
}
