package com.fundbridge.campaign.controller;

import com.fundbridge.campaign.dto.CampaignRequest;
import com.fundbridge.campaign.dto.CampaignResponse;
import com.fundbridge.campaign.dto.UpdateRaisedAmountRequest;
import com.fundbridge.campaign.entity.Campaign;
import com.fundbridge.campaign.service.CampaignService;
import com.fundbridge.common.dto.response.ApiResponse;
import com.fundbridge.common.enums.CampaignStatus;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/campaigns")
@RequiredArgsConstructor
public class CampaignController {

    private final CampaignService campaignService;

    // --- Public endpoints ---

    @GetMapping("/public/all")
    public ResponseEntity<ApiResponse<Page<CampaignResponse>>> getActiveCampaigns(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<CampaignResponse> response = campaignService.getActiveCampaigns(pageable)
                .map(CampaignResponse::fromEntity);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/public/{id}")
    public ResponseEntity<ApiResponse<CampaignResponse>> getCampaignById(@PathVariable Long id) {
        Campaign campaign = campaignService.getCampaignById(id);
        return ResponseEntity.ok(ApiResponse.success(CampaignResponse.fromEntity(campaign)));
    }

    @GetMapping("/public/search")
    public ResponseEntity<ApiResponse<Page<CampaignResponse>>> searchCampaigns(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<CampaignResponse> response = campaignService.searchCampaigns(keyword, pageable)
                .map(CampaignResponse::fromEntity);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // --- Authenticated endpoints ---

    @PostMapping
    public ResponseEntity<ApiResponse<CampaignResponse>> createCampaign(
            @Valid @RequestBody CampaignRequest request,
            @RequestHeader("X-User-Email") String email,
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader(value = "X-User-Name", required = false) String creatorName) {
        Campaign campaign = campaignService.createCampaign(request, email, userId, creatorName);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Campaign submitted for review", CampaignResponse.fromEntity(campaign)));
    }

    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<CampaignResponse>>> getMyCampaigns(
            @RequestHeader("X-User-Email") String email) {
        List<CampaignResponse> campaigns = campaignService.getMyCampaigns(email).stream()
                .map(CampaignResponse::fromEntity)
                .toList();
        return ResponseEntity.ok(ApiResponse.success(campaigns));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CampaignResponse>> updateCampaign(
            @PathVariable Long id,
            @Valid @RequestBody CampaignRequest request,
            @RequestHeader("X-User-Email") String email) {
        Campaign updated = campaignService.updateCampaign(id, request, email);
        return ResponseEntity.ok(ApiResponse.success("Campaign updated", CampaignResponse.fromEntity(updated)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCampaign(
            @PathVariable Long id,
            @RequestHeader("X-User-Email") String email) {
        campaignService.deleteCampaign(id, email);
        return ResponseEntity.ok(ApiResponse.success("Campaign deleted", null));
    }

    // --- Admin endpoints ---

    @GetMapping("/admin/all")
    public ResponseEntity<ApiResponse<Page<CampaignResponse>>> getCampaignsByStatus(
            @RequestParam(defaultValue = "PENDING_REVIEW") CampaignStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestHeader("X-User-Role") String role) {
        if (!role.equals("ROLE_ADMIN")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponse.error("Access denied"));
        }
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<CampaignResponse> response = campaignService.getCampaignsByStatus(status, pageable)
                .map(CampaignResponse::fromEntity);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<CampaignResponse>> updateStatus(
            @PathVariable Long id,
            @RequestParam CampaignStatus status,
            @RequestHeader("X-User-Role") String role) {
        if (!role.equals("ROLE_ADMIN")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponse.error("Access denied"));
        }
        Campaign updated = campaignService.updateCampaignStatus(id, status);
        return ResponseEntity.ok(ApiResponse.success("Campaign status updated", CampaignResponse.fromEntity(updated)));
    }

    // --- Internal endpoint for donation-service to update raised amount ---
    @PutMapping("/internal/{id}/raised-amount")
    public ResponseEntity<ApiResponse<CampaignResponse>> updateRaisedAmount(
            @PathVariable Long id,
            @RequestBody UpdateRaisedAmountRequest request) {
        Campaign updated = campaignService.updateRaisedAmount(id, request.getAdditionalAmount());
        return ResponseEntity.ok(ApiResponse.success("Raised amount updated", CampaignResponse.fromEntity(updated)));
    }
}
