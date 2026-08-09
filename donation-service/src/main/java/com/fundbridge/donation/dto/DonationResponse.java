package com.fundbridge.donation.dto;

import com.fundbridge.common.enums.PaymentStatus;
import com.fundbridge.donation.entity.Donation;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DonationResponse {

    private Long id;
    private BigDecimal amount;
    private String message;
    private boolean anonymous;
    private PaymentStatus paymentStatus;
    private Long campaignId;
    private String campaignTitle;
    private Long donorId;
    private String donorName;
    private LocalDateTime donatedAt;

    public static DonationResponse fromEntity(Donation donation) {
        boolean anon = donation.isAnonymous();
        return DonationResponse.builder()
                .id(donation.getId())
                .amount(donation.getAmount())
                .message(donation.getMessage())
                .anonymous(anon)
                .paymentStatus(donation.getPaymentStatus())
                .campaignId(donation.getCampaignId())
                .campaignTitle(donation.getCampaignTitle())
                .donorId(anon ? null : donation.getDonorId())
                .donorName(anon ? "Anonymous" : donation.getDonorName())
                .donatedAt(donation.getDonatedAt())
                .build();
    }
}
