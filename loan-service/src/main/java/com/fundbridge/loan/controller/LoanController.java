package com.fundbridge.loan.controller;

import com.fundbridge.common.dto.response.ApiResponse;
import com.fundbridge.loan.dto.*;
import com.fundbridge.loan.entity.LoanOffer;
import com.fundbridge.loan.entity.LoanRequest;
import com.fundbridge.loan.entity.RepaymentSchedule;
import com.fundbridge.loan.service.LoanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/loans")
@RequiredArgsConstructor
public class LoanController {

    private final LoanService loanService;

    @PostMapping("/request")
    public ResponseEntity<ApiResponse<LoanRequestResponse>> createLoanRequest(
            @Valid @RequestBody LoanRequestDto dto,
            @RequestHeader("X-User-Email") String email,
            @RequestHeader("X-User-Id") Long userId) {
        LoanRequest loan = loanService.createLoanRequest(dto, email, userId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Loan request submitted", LoanRequestResponse.fromEntity(loan)));
    }

    @GetMapping("/my-requests")
    public ResponseEntity<ApiResponse<List<LoanRequestResponse>>> getMyLoanRequests(
            @RequestHeader("X-User-Email") String email) {
        List<LoanRequestResponse> requests = loanService.getMyLoanRequests(email).stream()
                .map(loan -> {
                    LoanRequestResponse resp = LoanRequestResponse.fromEntity(loan);
                    resp.setLoanOffers(loanService.getOffersForLoanRequest(loan.getId()).stream()
                            .map(LoanOfferResponse::fromEntity).toList());
                    return resp;
                })
                .toList();
        return ResponseEntity.ok(ApiResponse.success(requests));
    }

    @GetMapping("/pending")
    public ResponseEntity<ApiResponse<Page<LoanRequestResponse>>> getPendingRequests(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<LoanRequestResponse> response = loanService.getPendingLoanRequests(pageable)
                .map(LoanRequestResponse::fromEntity);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<LoanRequestResponse>> getLoanById(@PathVariable Long id) {
        LoanRequest loan = loanService.getLoanRequestById(id);
        return ResponseEntity.ok(ApiResponse.success(LoanRequestResponse.fromEntity(loan)));
    }

    @PostMapping("/{loanRequestId}/offer")
    public ResponseEntity<ApiResponse<LoanOfferResponse>> submitOffer(
            @PathVariable Long loanRequestId,
            @Valid @RequestBody LoanOfferRequest request,
            @RequestHeader("X-User-Email") String email,
            @RequestHeader("X-User-Id") Long userId) {
        LoanOffer offer = loanService.submitLoanOffer(loanRequestId, request, email, userId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Loan offer submitted", LoanOfferResponse.fromEntity(offer)));
    }

    @PatchMapping("/offers/{offerId}/accept")
    public ResponseEntity<ApiResponse<LoanOfferResponse>> acceptOffer(
            @PathVariable Long offerId,
            @RequestHeader("X-User-Email") String email) {
        LoanOffer accepted = loanService.acceptLoanOffer(offerId, email);
        return ResponseEntity.ok(ApiResponse.success("Loan offer accepted", LoanOfferResponse.fromEntity(accepted)));
    }

    @PatchMapping("/offers/{offerId}/reject")
    public ResponseEntity<ApiResponse<LoanOfferResponse>> rejectOffer(
            @PathVariable Long offerId,
            @RequestHeader("X-User-Email") String email) {
        LoanOffer rejected = loanService.rejectLoanOffer(offerId, email);
        return ResponseEntity.ok(ApiResponse.success("Loan offer rejected", LoanOfferResponse.fromEntity(rejected)));
    }

    @GetMapping("/{id}/repayment-schedule")
    public ResponseEntity<ApiResponse<List<RepaymentScheduleResponse>>> getRepaymentSchedule(
            @PathVariable Long id,
            @RequestHeader("X-User-Email") String email) {
        List<RepaymentScheduleResponse> schedule = loanService.getRepaymentSchedule(id, email).stream()
                .map(RepaymentScheduleResponse::fromEntity)
                .toList();
        return ResponseEntity.ok(ApiResponse.success(schedule));
    }

    @PatchMapping("/repayment/{scheduleId}/pay")
    public ResponseEntity<ApiResponse<RepaymentScheduleResponse>> payInstallment(
            @PathVariable Long scheduleId,
            @RequestParam String razorpayPaymentId,
            @RequestHeader("X-User-Email") String email) {
        RepaymentSchedule paid = loanService.payInstallment(scheduleId, razorpayPaymentId, email);
        return ResponseEntity.ok(ApiResponse.success("Installment paid", RepaymentScheduleResponse.fromEntity(paid)));
    }
}
