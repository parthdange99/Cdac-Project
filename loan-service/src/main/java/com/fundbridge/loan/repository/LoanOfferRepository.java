package com.fundbridge.loan.repository;

import com.fundbridge.common.enums.OfferStatus;
import com.fundbridge.loan.entity.LoanOffer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoanOfferRepository extends JpaRepository<LoanOffer, Long> {
    List<LoanOffer> findByLoanRequestId(Long loanRequestId);
    List<LoanOffer> findByLoanRequestIdAndOfferStatus(Long loanRequestId, OfferStatus status);
}
