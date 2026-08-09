package com.fundbridge.donation.service.impl;

import com.fundbridge.common.exception.BadRequestException;
import com.fundbridge.common.exception.ResourceNotFoundException;
import com.fundbridge.donation.client.CampaignClient;
import com.fundbridge.donation.dto.DonationRequest;
import com.fundbridge.donation.entity.Donation;
import com.fundbridge.donation.repository.DonationRepository;
import com.fundbridge.donation.service.DonationService;
import com.fundbridge.common.enums.PaymentStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fundbridge.donation.client.NotificationClient;

@Service
@RequiredArgsConstructor
@Slf4j
public class DonationServiceImpl implements DonationService {

    private final DonationRepository donationRepository;
    private final CampaignClient campaignClient;
    private final NotificationClient notificationClient;

    @Override
    @Transactional
    public Donation initiateDonation(Long campaignId, DonationRequest request,
                                     String email, Long userId, String donorName) {
        // Verify campaign exists and is active via Feign
        try {
            var campaignResponse = campaignClient.getCampaignById(campaignId);
            if (campaignResponse.getBody() == null || !campaignResponse.getBody().isSuccess()) {
                throw new ResourceNotFoundException("Campaign", campaignId);
            }
            var campaignData = (Map<String, Object>) campaignResponse.getBody().getData();
            String status = (String) campaignData.get("status");
            if (!"ACTIVE".equals(status)) {
                throw new BadRequestException("Campaign is not active");
            }
            String campaignTitle = (String) campaignData.get("title");

            Donation donation = Donation.builder()
                    .amount(request.getAmount())
                    .message(request.getMessage())
                    .isAnonymous(request.isAnonymous())
                    .paymentStatus(PaymentStatus.PENDING)
                    .donorId(userId)
                    .donorEmail(email)
                    .donorName(donorName != null ? donorName : email)
                    .campaignId(campaignId)
                    .campaignTitle(campaignTitle)
                    .build();

            return donationRepository.save(donation);
        } catch (BadRequestException | ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to verify campaign {}: {}", campaignId, e.getMessage());
            throw new BadRequestException("Campaign verification failed: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public Donation confirmDonation(Long donationId, String razorpayPaymentId, String razorpayOrderId) {
        Donation donation = donationRepository.findById(donationId)
                .orElseThrow(() -> new ResourceNotFoundException("Donation", donationId));

        donation.setRazorpayPaymentId(razorpayPaymentId);
        donation.setRazorpayOrderId(razorpayOrderId);
        donation.setPaymentStatus(PaymentStatus.SUCCESS);

        // Update campaign raised amount via Feign
        try {
            Map<String, BigDecimal> body = new HashMap<>();
            body.put("additionalAmount", donation.getAmount());
            campaignClient.updateRaisedAmount(donation.getCampaignId(), body);
            
            // Fetch campaign details to get creator info
            var campaignResponse = campaignClient.getCampaignById(donation.getCampaignId());
            if (campaignResponse.getBody() != null && campaignResponse.getBody().isSuccess()) {
                var campaignData = (Map<String, Object>) campaignResponse.getBody().getData();
                Number creatorIdNum = (Number) campaignData.get("creatorId");
                Long creatorId = creatorIdNum != null ? creatorIdNum.longValue() : null;
                String creatorEmail = (String) campaignData.get("creatorEmail");
                
                if (creatorEmail != null) {
                    Map<String, Object> req = new HashMap<>();
                    req.put("userId", creatorId);
                    req.put("userEmail", creatorEmail);
                    req.put("title", "New Donation Received!");
                    String donorName = donation.isAnonymous() ? "An anonymous donor" : donation.getDonorName();
                    req.put("message", donorName + " just donated ₹" + donation.getAmount() + " to your campaign '" + donation.getCampaignTitle() + "'!");
                    req.put("notificationType", "NEW_DONATION");
                    notificationClient.sendNotification(req);
                }
            }
        } catch (Exception e) {
            log.error("Failed to update campaign or send notification: {}", e.getMessage());
            // Don't fail the donation confirmation
        }

        return donationRepository.save(donation);
    }

    @Override
    public List<Donation> getDonationsForCampaign(Long campaignId) {
        return donationRepository.findByCampaignId(campaignId);
    }

    @Override
    public List<Donation> getMyDonations(String email) {
        return donationRepository.findByDonorEmail(email);
    }
}
