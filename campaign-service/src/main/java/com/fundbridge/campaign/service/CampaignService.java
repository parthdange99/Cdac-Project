package com.fundbridge.campaign.service;

import com.fundbridge.campaign.dto.CampaignRequest;
import com.fundbridge.campaign.entity.Campaign;
import com.fundbridge.common.enums.CampaignStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

public interface CampaignService {
    Campaign createCampaign(CampaignRequest request, String email, Long userId, String creatorName);
    Campaign getCampaignById(Long id);
    Page<Campaign> getActiveCampaigns(Pageable pageable);
    Page<Campaign> getCampaignsByStatus(CampaignStatus status, Pageable pageable);
    Page<Campaign> searchCampaigns(String keyword, Pageable pageable);
    List<Campaign> getMyCampaigns(String email);
    Campaign updateCampaign(Long id, CampaignRequest request, String email);
    Campaign updateCampaignStatus(Long id, CampaignStatus status);
    Campaign updateRaisedAmount(Long id, BigDecimal additionalAmount);
    void deleteCampaign(Long id, String email);
}
