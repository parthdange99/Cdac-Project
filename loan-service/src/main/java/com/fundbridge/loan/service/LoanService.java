package com.fundbridge.loan.service;

import com.fundbridge.loan.dto.LoanOfferRequest;
import com.fundbridge.loan.dto.LoanRequestDto;
import com.fundbridge.loan.entity.LoanOffer;
import com.fundbridge.loan.entity.LoanRequest;
import com.fundbridge.loan.entity.RepaymentSchedule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface LoanService {
    LoanRequest createLoanRequest(LoanRequestDto dto, String email, Long userId);
    LoanRequest getLoanRequestById(Long id);
    Page<LoanRequest> getPendingLoanRequests(Pageable pageable);
    List<LoanRequest> getMyLoanRequests(String email);
    LoanOffer submitLoanOffer(Long loanRequestId, LoanOfferRequest request, String lenderEmail, Long lenderId);
    LoanOffer acceptLoanOffer(Long offerId, String borrowerEmail);
    LoanOffer rejectLoanOffer(Long offerId, String borrowerEmail);
    List<LoanOffer> getOffersForLoanRequest(Long loanRequestId);
    List<RepaymentSchedule> getRepaymentSchedule(Long loanRequestId, String email);
    RepaymentSchedule payInstallment(Long scheduleId, String razorpayPaymentId, String email);
}
