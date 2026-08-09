package com.fundbridge.donation.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class DonationRequest {

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "1.00", message = "Minimum donation is Rs 1")
    private BigDecimal amount;

    private String message;
    private boolean anonymous = false;
}
