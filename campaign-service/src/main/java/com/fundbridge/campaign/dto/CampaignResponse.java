package com.fundbridge.campaign.dto;

import com.fundbridge.campaign.entity.Campaign;
import com.fundbridge.common.enums.CampaignStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CampaignResponse {

    private Long id;
    private String title;
    private String description;
    private BigDecimal goalAmount;
    private BigDecimal raisedAmount;
    private LocalDate startDate;
    private LocalDate endDate;
    private String imageUrl;
    private CampaignStatus status;
    private String category;
    private Long creatorId;
    private String creatorEmail;
    private String creatorName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static CampaignResponse fromEntity(Campaign campaign) {
        return CampaignResponse.builder()
                .id(campaign.getId())
                .title(campaign.getTitle())
                .description(campaign.getDescription())
                .goalAmount(campaign.getGoalAmount())
                .raisedAmount(campaign.getRaisedAmount())
                .startDate(campaign.getStartDate())
                .endDate(campaign.getEndDate())
                .imageUrl(campaign.getImageUrl())
                .status(campaign.getStatus())
                .category(campaign.getCategory())
                .creatorId(campaign.getCreatorId())
                .creatorEmail(campaign.getCreatorEmail())
                .creatorName(campaign.getCreatorName())
                .createdAt(campaign.getCreatedAt())
                .updatedAt(campaign.getUpdatedAt())
                .build();
    }
}
