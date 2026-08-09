package com.fundbridge.loan.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class LoanOfferRequest {

    @NotNull(message = "Offered amount is required")
    @DecimalMin(value = "1000.00", message = "Minimum offer is Rs 1000")
    private BigDecimal offeredAmount;

    @NotNull(message = "Interest rate is required")
    @DecimalMin(value = "0.0", message = "Interest rate cannot be negative")
    @DecimalMax(value = "36.0", message = "Maximum interest rate is 36%")
    private BigDecimal offeredInterestRate;

    private String message;
}
