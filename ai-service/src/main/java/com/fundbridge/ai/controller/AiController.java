package com.fundbridge.ai.controller;

import com.fundbridge.ai.dto.AiRequest;
import com.fundbridge.ai.service.AiService;
import com.fundbridge.common.dto.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiService aiService;

    @PostMapping("/generate-description")
    public ResponseEntity<ApiResponse<Map<String, String>>> generateDescription(
            @RequestBody AiRequest request) {
        String description = aiService.generateCampaignDescription(
                request.getTitle(), request.getCategory());
        return ResponseEntity.ok(ApiResponse.success(Map.of("description", description)));
    }

    @PostMapping("/suggest-donation-message")
    public ResponseEntity<ApiResponse<Map<String, String>>> suggestDonationMessage(
            @RequestBody AiRequest request) {
        String message = aiService.suggestDonationMessage(
                request.getDonorName(), request.getCampaignTitle());
        return ResponseEntity.ok(ApiResponse.success(Map.of("message", message)));
    }
}
