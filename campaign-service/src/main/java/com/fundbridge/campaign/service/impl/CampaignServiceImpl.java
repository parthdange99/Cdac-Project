package com.fundbridge.campaign.service.impl;

import com.fundbridge.campaign.dto.CampaignRequest;
import com.fundbridge.campaign.entity.Campaign;
import com.fundbridge.campaign.repository.CampaignRepository;
import com.fundbridge.campaign.service.CampaignService;
import com.fundbridge.common.enums.CampaignStatus;
import com.fundbridge.common.exception.BadRequestException;
import com.fundbridge.common.exception.ResourceNotFoundException;
import com.fundbridge.common.exception.UnauthorizedException;
import com.fundbridge.campaign.client.NotificationClient;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CampaignServiceImpl implements CampaignService {

    private final CampaignRepository campaignRepository;
    private final NotificationClient notificationClient;

    @Override
    @Transactional
    public Campaign createCampaign(CampaignRequest request, String email, Long userId, String creatorName) {
        Campaign campaign = Campaign.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .goalAmount(request.getGoalAmount())
                .startDate(LocalDate.now())
                .endDate(request.getEndDate())
                .imageUrl(request.getImageUrl())
                .category(request.getCategory())
                .status(CampaignStatus.PENDING_REVIEW)
                .creatorId(userId)
                .creatorEmail(email)
                .creatorName(creatorName != null ? creatorName : email)
                .build();
        return campaignRepository.save(campaign);
    }

    @Override
    public Campaign getCampaignById(Long id) {
        return campaignRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Campaign", id));
    }

    @Override
    public Page<Campaign> getActiveCampaigns(Pageable pageable) {
        return campaignRepository.findByStatus(CampaignStatus.ACTIVE, pageable);
    }

    @Override
    public Page<Campaign> getCampaignsByStatus(CampaignStatus status, Pageable pageable) {
        return campaignRepository.findByStatus(status, pageable);
    }

    @Override
    public Page<Campaign> searchCampaigns(String keyword, Pageable pageable) {
        return campaignRepository.searchActiveCampaigns(keyword, pageable);
    }

    @Override
    public List<Campaign> getMyCampaigns(String email) {
        return campaignRepository.findByCreatorEmail(email);
    }

    @Override
    @Transactional
    public Campaign updateCampaign(Long id, CampaignRequest request, String email) {
        Campaign campaign = getCampaignById(id);
        if (!campaign.getCreatorEmail().equals(email)) {
            throw new UnauthorizedException("You do not have permission to modify this campaign");
        }
        campaign.setTitle(request.getTitle());
        campaign.setDescription(request.getDescription());
        campaign.setGoalAmount(request.getGoalAmount());
        campaign.setEndDate(request.getEndDate());
        campaign.setImageUrl(request.getImageUrl());
        campaign.setCategory(request.getCategory());
        return campaignRepository.save(campaign);
    }

    @Override
    @Transactional
    public Campaign updateCampaignStatus(Long id, CampaignStatus status) {
        Campaign campaign = getCampaignById(id);
        campaign.setStatus(status);
        Campaign saved = campaignRepository.save(campaign);
        
        try {
            Map<String, Object> req = new HashMap<>();
            req.put("userId", campaign.getCreatorId());
            req.put("userEmail", campaign.getCreatorEmail());
            req.put("title", "Campaign " + status.name());
            req.put("message", "Your campaign '" + campaign.getTitle() + "' status has been updated to " + status.name());
            req.put("notificationType", "CAMPAIGN_UPDATE");
            notificationClient.sendNotification(req);
        } catch (Exception e) {
            log.error("Failed to send notification for campaign {}: {}", id, e.getMessage());
        }
        
        return saved;
    }

    @Override
    @Transactional
    public Campaign updateRaisedAmount(Long id, BigDecimal additionalAmount) {
        Campaign campaign = getCampaignById(id);
        BigDecimal newAmount = campaign.getRaisedAmount().add(additionalAmount);
        campaign.setRaisedAmount(newAmount);
        // Auto-complete if goal reached
        if (newAmount.compareTo(campaign.getGoalAmount()) >= 0) {
            campaign.setStatus(CampaignStatus.COMPLETED);
        }
        return campaignRepository.save(campaign);
    }

    @Override
    @Transactional
    public void deleteCampaign(Long id, String email) {
        Campaign campaign = getCampaignById(id);
        if (!campaign.getCreatorEmail().equals(email)) {
            throw new UnauthorizedException("You do not have permission to delete this campaign");
        }
        campaignRepository.delete(campaign);
    }
}
