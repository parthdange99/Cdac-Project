package com.fundbridge.loan.entity;

import com.fundbridge.common.enums.OfferStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "loan_offers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoanOffer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "offered_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal offeredAmount;

    @Column(name = "offered_interest_rate", precision = 5, scale = 2)
    private BigDecimal offeredInterestRate;

    @Column(length = 500)
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(name = "offer_status", length = 30, nullable = false)
    @Builder.Default
    private OfferStatus offerStatus = OfferStatus.PENDING;

    // Store lender ID (references auth-service user ID)
    @Column(name = "lender_id", nullable = false)
    private Long lenderId;

    @Column(name = "lender_email", nullable = false)
    private String lenderEmail;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loan_request_id", nullable = false)
    private LoanRequest loanRequest;

    @CreationTimestamp
    @Column(name = "offered_at", updatable = false)
    private LocalDateTime offeredAt;
}
