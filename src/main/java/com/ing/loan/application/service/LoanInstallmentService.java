package com.ing.loan.application.service;

import com.ing.loan.application.entity.LoanInstallment;
import com.ing.loan.application.repository.LoanInstallmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

@Service
public class LoanInstallmentService {
    @Autowired
    private LoanInstallmentRepository loanInstallmentRepository;

    public void saveAll(List<LoanInstallment> loanInstallmentsList) {
        loanInstallmentRepository.saveAll(loanInstallmentsList);
    }

    public LoanInstallment save(LoanInstallment loanInstallment) {
        return loanInstallmentRepository.save(loanInstallment);
    }

    public List<LoanInstallment> findAllByLoanId(long loanId) {
        return loanInstallmentRepository.findAllByLoanId(loanId);
    }

    public List<LoanInstallment> findAllByLoanIdAndPaidStatus(long loanId, boolean isPaid) {
        return loanInstallmentRepository.findAllByLoanIdAndIsPaidOrderByDueDateAsc(loanId, isPaid);
    }

    public List<LoanInstallment> findAllByLoanIdAndPaidStatusAndDueDate(long loanId, boolean isPaid, Timestamp timestamp) {
        return loanInstallmentRepository.findAllByLoanIdAndIsPaidAndDueDateBeforeOrderByDueDateAsc(loanId, isPaid, timestamp);
    }

    void updateStatusAsPaid(long id) {
        loanInstallmentRepository.updateStatusAsPaid(id);
    }
}
