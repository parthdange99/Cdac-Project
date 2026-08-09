package com.fundbridge.campaign.repository;

import com.fundbridge.campaign.entity.Campaign;
import com.fundbridge.common.enums.CampaignStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CampaignRepository extends JpaRepository<Campaign, Long> {
    Page<Campaign> findByStatus(CampaignStatus status, Pageable pageable);
    List<Campaign> findByCreatorId(Long creatorId);
    List<Campaign> findByCreatorEmail(String email);

    @Query("SELECT c FROM Campaign c WHERE c.status = 'ACTIVE' AND " +
           "(LOWER(c.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(c.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(c.category) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Campaign> searchActiveCampaigns(@Param("keyword") String keyword, Pageable pageable);
}
