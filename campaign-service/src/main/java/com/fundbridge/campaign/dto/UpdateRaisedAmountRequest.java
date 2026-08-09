package com.fundbridge.campaign.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class UpdateRaisedAmountRequest {
    private BigDecimal additionalAmount;
}
