package com.fundbridge.loan.repository;

import com.fundbridge.common.enums.RepaymentStatus;
import com.fundbridge.loan.entity.RepaymentSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RepaymentScheduleRepository extends JpaRepository<RepaymentSchedule, Long> {
    List<RepaymentSchedule> findByLoanRequestId(Long loanRequestId);
    List<RepaymentSchedule> findByLoanRequestIdAndStatus(Long loanRequestId, RepaymentStatus status);
}
