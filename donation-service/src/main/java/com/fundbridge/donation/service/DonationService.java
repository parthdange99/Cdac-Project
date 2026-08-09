package com.fundbridge.donation.service;

import com.fundbridge.donation.dto.DonationRequest;
import com.fundbridge.donation.entity.Donation;

import java.util.List;

public interface DonationService {
    Donation initiateDonation(Long campaignId, DonationRequest request, String email, Long userId, String donorName);
    Donation confirmDonation(Long donationId, String razorpayPaymentId, String razorpayOrderId);
    List<Donation> getDonationsForCampaign(Long campaignId);
    List<Donation> getMyDonations(String email);
}
