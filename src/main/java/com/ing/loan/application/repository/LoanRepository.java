package com.ing.loan.application.repository;

import com.ing.loan.application.entity.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {
    List<Loan> findAllByCustomerId(long customerId);
    Loan findFirstByCustomerId(long customerId);
}
