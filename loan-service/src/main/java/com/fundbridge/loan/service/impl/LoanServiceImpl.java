package com.fundbridge.loan.service.impl;

import com.fundbridge.common.enums.LoanStatus;
import com.fundbridge.common.enums.OfferStatus;
import com.fundbridge.common.enums.RepaymentStatus;
import com.fundbridge.common.exception.BadRequestException;
import com.fundbridge.common.exception.ResourceNotFoundException;
import com.fundbridge.common.exception.UnauthorizedException;
import com.fundbridge.loan.dto.LoanOfferRequest;
import com.fundbridge.loan.dto.LoanRequestDto;
import com.fundbridge.loan.entity.LoanOffer;
import com.fundbridge.loan.entity.LoanRequest;
import com.fundbridge.loan.entity.RepaymentSchedule;
import com.fundbridge.loan.repository.LoanOfferRepository;
import com.fundbridge.loan.repository.LoanRequestRepository;
import com.fundbridge.loan.repository.RepaymentScheduleRepository;
import com.fundbridge.loan.service.LoanService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LoanServiceImpl implements LoanService {

    private final LoanRequestRepository loanRequestRepository;
    private final LoanOfferRepository loanOfferRepository;
    private final RepaymentScheduleRepository repaymentScheduleRepository;

    @Override
    @Transactional
    public LoanRequest createLoanRequest(LoanRequestDto dto, String email, Long userId) {
        LoanRequest loanRequest = LoanRequest.builder()
                .amount(dto.getAmount())
                .tenureMonths(dto.getTenureMonths())
                .purpose(dto.getPurpose())
                .monthlyIncome(dto.getMonthlyIncome())
                .creditScore(dto.getCreditScore())
                .status(LoanStatus.PENDING)
                .borrowerId(userId)
                .borrowerEmail(email)
                .build();

        return loanRequestRepository.save(loanRequest);
    }

    @Override
    public LoanRequest getLoanRequestById(Long id) {
        return loanRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Loan Request", id));
    }

    @Override
    public Page<LoanRequest> getPendingLoanRequests(Pageable pageable) {
        return loanRequestRepository.findByStatus(LoanStatus.PENDING, pageable);
    }

    @Override
    public List<LoanRequest> getMyLoanRequests(String email) {
        return loanRequestRepository.findByBorrowerEmail(email);
    }

    @Override
    @Transactional
    public LoanOffer submitLoanOffer(Long loanRequestId, LoanOfferRequest request, String lenderEmail, Long lenderId) {
        LoanRequest loanRequest = getLoanRequestById(loanRequestId);
        if (loanRequest.getStatus() != LoanStatus.PENDING) {
            throw new BadRequestException("This loan request is no longer accepting offers");
        }

        LoanOffer offer = LoanOffer.builder()
                .offeredAmount(request.getOfferedAmount())
                .offeredInterestRate(request.getOfferedInterestRate())
                .message(request.getMessage())
                .offerStatus(OfferStatus.PENDING)
                .lenderId(lenderId)
                .lenderEmail(lenderEmail)
                .loanRequest(loanRequest)
                .build();

        return loanOfferRepository.save(offer);
    }

    @Override
    @Transactional
    public LoanOffer acceptLoanOffer(Long offerId, String borrowerEmail) {
        LoanOffer offer = loanOfferRepository.findById(offerId)
                .orElseThrow(() -> new ResourceNotFoundException("Loan Offer", offerId));

        LoanRequest loanRequest = offer.getLoanRequest();
        if (!loanRequest.getBorrowerEmail().equals(borrowerEmail)) {
            throw new UnauthorizedException("You are not the borrower for this loan");
        }

        offer.setOfferStatus(OfferStatus.ACCEPTED);
        loanRequest.setStatus(LoanStatus.APPROVED);
        loanRequest.setInterestRate(offer.getOfferedInterestRate());

        // Reject other pending offers
        List<LoanOffer> otherOffers = loanOfferRepository
                .findByLoanRequestIdAndOfferStatus(loanRequest.getId(), OfferStatus.PENDING);
        otherOffers.stream()
                .filter(o -> !o.getId().equals(offerId))
                .forEach(o -> o.setOfferStatus(OfferStatus.REJECTED));
        loanOfferRepository.saveAll(otherOffers);

        // Generate repayment schedule
        generateRepaymentSchedule(loanRequest, offer.getOfferedInterestRate());

        loanRequestRepository.save(loanRequest);
        return loanOfferRepository.save(offer);
    }

    @Override
    @Transactional
    public LoanOffer rejectLoanOffer(Long offerId, String borrowerEmail) {
        LoanOffer offer = loanOfferRepository.findById(offerId)
                .orElseThrow(() -> new ResourceNotFoundException("Loan Offer", offerId));
        if (!offer.getLoanRequest().getBorrowerEmail().equals(borrowerEmail)) {
            throw new UnauthorizedException("You are not the borrower for this loan");
        }
        offer.setOfferStatus(OfferStatus.REJECTED);
        return loanOfferRepository.save(offer);
    }

    @Override
    public List<LoanOffer> getOffersForLoanRequest(Long loanRequestId) {
        return loanOfferRepository.findByLoanRequestId(loanRequestId);
    }

    @Override
    public List<RepaymentSchedule> getRepaymentSchedule(Long loanRequestId, String email) {
        LoanRequest loan = getLoanRequestById(loanRequestId);
        if (!loan.getBorrowerEmail().equals(email)) {
            throw new UnauthorizedException("Access denied");
        }
        return repaymentScheduleRepository.findByLoanRequestId(loanRequestId);
    }

    @Override
    @Transactional
    public RepaymentSchedule payInstallment(Long scheduleId, String razorpayPaymentId, String email) {
        RepaymentSchedule schedule = repaymentScheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ResourceNotFoundException("Repayment Schedule", scheduleId));

        if (schedule.getStatus() == RepaymentStatus.PAID) {
            throw new BadRequestException("This installment is already paid");
        }

        schedule.setStatus(RepaymentStatus.PAID);
        schedule.setPaidAt(LocalDateTime.now());
        schedule.setRazorpayPaymentId(razorpayPaymentId);

        // Check if all installments paid -> close loan
        LoanRequest loan = schedule.getLoanRequest();
        List<RepaymentSchedule> pending = repaymentScheduleRepository
                .findByLoanRequestIdAndStatus(loan.getId(), RepaymentStatus.PENDING);
        if (pending.size() <= 1) { // current one is still PENDING in DB, so <= 1
            loan.setStatus(LoanStatus.CLOSED);
            loanRequestRepository.save(loan);
        }

        return repaymentScheduleRepository.save(schedule);
    }

    private void generateRepaymentSchedule(LoanRequest loan, BigDecimal annualRate) {
        BigDecimal principal = loan.getAmount();
        int months = loan.getTenureMonths();
        BigDecimal monthlyRate = annualRate.divide(BigDecimal.valueOf(1200), 10, RoundingMode.HALF_UP);

        // EMI = P * r * (1+r)^n / ((1+r)^n - 1)
        BigDecimal onePlusR = BigDecimal.ONE.add(monthlyRate);
        BigDecimal onePlusRPowN = onePlusR.pow(months, new MathContext(10));
        BigDecimal emi = principal
                .multiply(monthlyRate)
                .multiply(onePlusRPowN)
                .divide(onePlusRPowN.subtract(BigDecimal.ONE), 2, RoundingMode.HALF_UP);

        List<RepaymentSchedule> schedules = new ArrayList<>();
        BigDecimal balance = principal;

        for (int i = 1; i <= months; i++) {
            BigDecimal interest = balance.multiply(monthlyRate).setScale(2, RoundingMode.HALF_UP);
            BigDecimal principalComponent = emi.subtract(interest);
            balance = balance.subtract(principalComponent);

            RepaymentSchedule schedule = RepaymentSchedule.builder()
                    .installmentNumber(i)
                    .dueDate(LocalDate.now().plusMonths(i))
                    .emiAmount(emi)
                    .principalComponent(principalComponent)
                    .interestComponent(interest)
                    .status(RepaymentStatus.PENDING)
                    .loanRequest(loan)
                    .build();
            schedules.add(schedule);
        }
        repaymentScheduleRepository.saveAll(schedules);
    }
}
