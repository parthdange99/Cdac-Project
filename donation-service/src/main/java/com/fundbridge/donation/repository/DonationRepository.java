package com.fundbridge.donation.repository;

import com.fundbridge.donation.entity.Donation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DonationRepository extends JpaRepository<Donation, Long> {
    List<Donation> findByCampaignId(Long campaignId);
    List<Donation> findByDonorId(Long donorId);
    List<Donation> findByDonorEmail(String email);
}
