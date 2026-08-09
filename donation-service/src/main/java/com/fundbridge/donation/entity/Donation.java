package com.fundbridge.donation.entity;

import com.fundbridge.common.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "donations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Donation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(length = 500)
    private String message;

    @Column(name = "is_anonymous")
    @Builder.Default
    private boolean isAnonymous = false;

    @Column(name = "razorpay_payment_id", length = 100)
    private String razorpayPaymentId;

    @Column(name = "razorpay_order_id", length = 100)
    private String razorpayOrderId;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", length = 30, nullable = false)
    @Builder.Default
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;

    // Donor info (from gateway headers - auth-service user)
    @Column(name = "donor_id", nullable = false)
    private Long donorId;

    @Column(name = "donor_email", nullable = false)
    private String donorEmail;

    @Column(name = "donor_name", length = 100)
    private String donorName;

    // Campaign reference
    @Column(name = "campaign_id", nullable = false)
    private Long campaignId;

    @Column(name = "campaign_title", length = 200)
    private String campaignTitle;

    @CreationTimestamp
    @Column(name = "donated_at", updatable = false)
    private LocalDateTime donatedAt;
}
