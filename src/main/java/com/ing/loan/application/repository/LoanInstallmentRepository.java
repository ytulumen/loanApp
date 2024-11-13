package com.ing.loan.application.repository;

import com.ing.loan.application.entity.LoanInstallment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface LoanInstallmentRepository extends JpaRepository<LoanInstallment, Long> {
    @Query("UPDATE LoanInstallment SET isPaid = true WHERE id = :id")
    @Modifying
    void updateStatusAsPaid(long id);
    List<LoanInstallment> findAllByLoanId(long loanId);
    List<LoanInstallment> findAllByLoanIdAndIsPaidOrderByDueDateAsc(long loanId, boolean isPaid);
    List<LoanInstallment> findAllByLoanIdAndIsPaidAndDueDateBeforeOrderByDueDateAsc(long loanId, boolean isPaid, Timestamp timestamp);
}
