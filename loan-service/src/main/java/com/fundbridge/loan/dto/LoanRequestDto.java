package com.fundbridge.loan.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class LoanRequestDto {

    @NotNull(message = "Loan amount is required")
    @DecimalMin(value = "1000.00", message = "Minimum loan amount is Rs 1000")
    private BigDecimal amount;

    @NotNull(message = "Tenure is required")
    @Min(value = 1, message = "Minimum tenure is 1 month")
    @Max(value = 60, message = "Maximum tenure is 60 months")
    private Integer tenureMonths;

    @NotBlank(message = "Purpose is required")
    private String purpose;

    private BigDecimal monthlyIncome;
    private Integer creditScore;
}
