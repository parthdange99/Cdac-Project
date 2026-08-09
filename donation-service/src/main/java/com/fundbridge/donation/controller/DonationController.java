package com.fundbridge.donation.controller;

import com.fundbridge.common.dto.response.ApiResponse;
import com.fundbridge.donation.dto.DonationRequest;
import com.fundbridge.donation.dto.DonationResponse;
import com.fundbridge.donation.entity.Donation;
import com.fundbridge.donation.service.DonationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/donations")
@RequiredArgsConstructor
public class DonationController {

    private final DonationService donationService;

    @PostMapping("/campaign/{campaignId}")
    public ResponseEntity<ApiResponse<DonationResponse>> initiateDonation(
            @PathVariable Long campaignId,
            @Valid @RequestBody DonationRequest request,
            @RequestHeader("X-User-Email") String email,
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader(value = "X-User-Name", required = false) String donorName) {
        Donation donation = donationService.initiateDonation(campaignId, request, email, userId, donorName);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Donation initiated", DonationResponse.fromEntity(donation)));
    }

    @PatchMapping("/{donationId}/confirm")
    public ResponseEntity<ApiResponse<DonationResponse>> confirmDonation(
            @PathVariable Long donationId,
            @RequestParam String razorpayPaymentId,
            @RequestParam String razorpayOrderId) {
        Donation confirmed = donationService.confirmDonation(donationId, razorpayPaymentId, razorpayOrderId);
        return ResponseEntity.ok(ApiResponse.success("Donation confirmed", DonationResponse.fromEntity(confirmed)));
    }

    @GetMapping("/campaign/{campaignId}")
    public ResponseEntity<ApiResponse<List<DonationResponse>>> getDonationsForCampaign(
            @PathVariable Long campaignId) {
        List<DonationResponse> donations = donationService.getDonationsForCampaign(campaignId).stream()
                .map(DonationResponse::fromEntity)
                .toList();
        return ResponseEntity.ok(ApiResponse.success(donations));
    }

    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<DonationResponse>>> getMyDonations(
            @RequestHeader("X-User-Email") String email) {
        List<DonationResponse> donations = donationService.getMyDonations(email).stream()
                .map(DonationResponse::fromEntity)
                .toList();
        return ResponseEntity.ok(ApiResponse.success(donations));
    }
}
