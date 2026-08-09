package com.fundbridge.loan.dto;

import com.fundbridge.common.enums.LoanStatus;
import com.fundbridge.loan.entity.LoanRequest;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoanRequestResponse {

    private Long id;
    private BigDecimal amount;
    private BigDecimal interestRate;
    private Integer tenureMonths;
    private String purpose;
    private LoanStatus status;
    private Integer creditScore;
    private BigDecimal monthlyIncome;
    private Long borrowerId;
    private String borrowerEmail;
    private List<LoanOfferResponse> loanOffers;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static LoanRequestResponse fromEntity(LoanRequest loan) {
        return LoanRequestResponse.builder()
                .id(loan.getId())
                .amount(loan.getAmount())
                .interestRate(loan.getInterestRate())
                .tenureMonths(loan.getTenureMonths())
                .purpose(loan.getPurpose())
                .status(loan.getStatus())
                .creditScore(loan.getCreditScore())
                .monthlyIncome(loan.getMonthlyIncome())
                .borrowerId(loan.getBorrowerId())
                .borrowerEmail(loan.getBorrowerEmail())
                .createdAt(loan.getCreatedAt())
                .updatedAt(loan.getUpdatedAt())
                .build();
    }
}
