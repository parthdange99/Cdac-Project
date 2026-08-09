package com.fundbridge.loan.repository;

import com.fundbridge.common.enums.LoanStatus;
import com.fundbridge.loan.entity.LoanRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoanRequestRepository extends JpaRepository<LoanRequest, Long> {
    Page<LoanRequest> findByStatus(LoanStatus status, Pageable pageable);
    List<LoanRequest> findByBorrowerId(Long borrowerId);
    List<LoanRequest> findByBorrowerEmail(String email);
}
