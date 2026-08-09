package com.fundbridge.loan.dto;

import com.fundbridge.common.enums.RepaymentStatus;
import com.fundbridge.loan.entity.RepaymentSchedule;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RepaymentScheduleResponse {

    private Long id;
    private Integer installmentNumber;
    private LocalDate dueDate;
    private BigDecimal emiAmount;
    private BigDecimal principalComponent;
    private BigDecimal interestComponent;
    private RepaymentStatus status;
    private LocalDateTime paidAt;
    private Long loanRequestId;

    public static RepaymentScheduleResponse fromEntity(RepaymentSchedule schedule) {
        return RepaymentScheduleResponse.builder()
                .id(schedule.getId())
                .installmentNumber(schedule.getInstallmentNumber())
                .dueDate(schedule.getDueDate())
                .emiAmount(schedule.getEmiAmount())
                .principalComponent(schedule.getPrincipalComponent())
                .interestComponent(schedule.getInterestComponent())
                .status(schedule.getStatus())
                .paidAt(schedule.getPaidAt())
                .loanRequestId(schedule.getLoanRequest() != null ? schedule.getLoanRequest().getId() : null)
                .build();
    }
}
