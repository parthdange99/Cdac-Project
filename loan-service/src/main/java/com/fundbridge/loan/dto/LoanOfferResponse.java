package com.fundbridge.loan.dto;

import com.fundbridge.common.enums.OfferStatus;
import com.fundbridge.loan.entity.LoanOffer;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoanOfferResponse {

    private Long id;
    private BigDecimal offeredAmount;
    private BigDecimal offeredInterestRate;
    private String message;
    private OfferStatus offerStatus;
    private Long lenderId;
    private String lenderEmail;
    private Long loanRequestId;
    private LocalDateTime offeredAt;

    public static LoanOfferResponse fromEntity(LoanOffer offer) {
        return LoanOfferResponse.builder()
                .id(offer.getId())
                .offeredAmount(offer.getOfferedAmount())
                .offeredInterestRate(offer.getOfferedInterestRate())
                .message(offer.getMessage())
                .offerStatus(offer.getOfferStatus())
                .lenderId(offer.getLenderId())
                .lenderEmail(offer.getLenderEmail())
                .loanRequestId(offer.getLoanRequest() != null ? offer.getLoanRequest().getId() : null)
                .offeredAt(offer.getOfferedAt())
                .build();
    }
}
