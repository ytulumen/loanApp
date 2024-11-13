package com.ing.loan.application.service;

import com.ing.loan.application.entity.Customer;
import com.ing.loan.application.entity.Loan;
import com.ing.loan.application.entity.LoanInstallment;
import com.ing.loan.application.exception.CreditLimitException;
import com.ing.loan.application.exception.IllegalInterestRateException;
import com.ing.loan.application.exception.InstallmentNumberException;
import com.ing.loan.application.exception.UnauthorizedException;
import com.ing.loan.application.model.*;
import com.ing.loan.application.repository.LoanRepository;
import com.ing.loan.application.util.Util;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class LoanService {
    @Value("${ing.loanApplication.minInterestRate}")
    private float minInterestRate;
    @Value("${ing.loanApplication.maxInterestRate}")
    private float maxInterestRate;
    @Value("${ing.loanApplication.installments}")
    private String installments;
    private final LoanRepository loanRepository;
    private final CustomerService customerService;
    private final LoanInstallmentService loanInstallmentService;

    private final UserService userService;

    private final ZoneId zoneId = ZoneId.of("Europe/Istanbul");

    public LoanService(LoanRepository loanRepository, CustomerService customerService, LoanInstallmentService loanInstallmentService, UserService userService) {
        this.loanRepository = loanRepository;
        this.customerService = customerService;
        this.loanInstallmentService = loanInstallmentService;
        this.userService = userService;
    }

    @Transactional
    public PayLoanResponseModel payLoan(PayLoanRequestModel payLoanRequest) {
        Timestamp timestamp = Timestamp.valueOf(LocalDate.now(zoneId).with(TemporalAdjusters.firstDayOfNextMonth()).plusDays(1).plusMonths(2).atStartOfDay());
        PayLoanResponseModel payLoanResponseModel = new PayLoanResponseModel();
        Loan loan = loanRepository.findById(payLoanRequest.getLoanId()).orElseThrow();
        List<LoanInstallment> loanInstallments = loanInstallmentService.findAllByLoanIdAndPaidStatusAndDueDate(loan.getId(), false, timestamp);
        BigDecimal amount = BigDecimal.valueOf(payLoanRequest.getPayAmount().doubleValue());

        for (LoanInstallment loanInstallment:  loanInstallments) {
            long diffAsMillis = new Timestamp(System.currentTimeMillis()).getTime() - loanInstallment.getDueDate().getTime();
            long diffAsDays = TimeUnit.MILLISECONDS.toDays(diffAsMillis);
            BigDecimal amountMustBePaid = loanInstallment.getAmount().add(BigDecimal.valueOf(0.001 * diffAsDays));

            if (amount.compareTo(amountMustBePaid) > -1) {
                amount = amount.subtract(amountMustBePaid);
                loanInstallment.setPaid(true);
                loanInstallment.setPaidAmount(amountMustBePaid);
                loanInstallment.setPaymentDate(new Timestamp(System.currentTimeMillis()));
                loanInstallmentService.save(loanInstallment);

                payLoanResponseModel.setTotalAmountSpent(payLoanResponseModel.getTotalAmountSpent().add(amountMustBePaid));
                payLoanResponseModel.getPaidInstallments().add(loanInstallment);
            } else {
                break;
            }
        }
        payLoanResponseModel.setChange(amount);

        if (loanInstallmentService.findAllByLoanIdAndPaidStatus(loan.getId(), false).isEmpty()) {
            payLoanResponseModel.setLoanPaidCompletely(true);
            customerService.creditPaidBack(customerService.findById(loan.getCustomerId()), loan.getLoanAmount());
        }

        return payLoanResponseModel;
    }

    public List<LoanInstallment> getLoanInstallments(long customerId, long loanId, String username) throws UnauthorizedException {
        List<LoanInstallment> loanInstallment = loanInstallmentService.findAllByLoanId(loanId);
        Loan loan = loanRepository.findById(loanInstallment.get(0).getLoanId()).orElseThrow();

        if (loan.getCustomerId() != customerId && !userService.isUserAdmin(username)) {
            throw new UnauthorizedException();
        }

        return loanInstallment;
    }

    public List<LoanResponseModel> listLoans(long customerId) {
        List<Loan> loans = loanRepository.findAllByCustomerId(customerId);
        List<LoanResponseModel> loanResponseModel = new ArrayList<>();

        for (Loan loan: loans) {
            BigDecimal loanRepayment = loanInstallmentService.findAllByLoanId(loan.getId()).stream().map(LoanInstallment::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
            loanResponseModel.add(
                    new LoanResponseModel(
                            loan.getCustomerId(),
                            loan.getLoanAmount(),
                            loanRepayment,
                            loan.getNumberOfInstallment(),
                            loan.getCreateDate(),
                            loan.isPaid()
                    )
            );
        }
        return loanResponseModel;
    }

    @Transactional
    public CreateLoanResponseModel create(CreateLoanRequestModel createLoanRequestModel)
            throws InstallmentNumberException, IllegalInterestRateException, CreditLimitException {
        Customer customer = customerService.findById(createLoanRequestModel.getCustomerId());
        BigDecimal remainingCreditLimit = customer.getCreditLimit().subtract(customer.getUsedCreditLimit());

        if (remainingCreditLimit.compareTo(createLoanRequestModel.getAmount()) < 0) {
            throw new CreditLimitException(remainingCreditLimit);
        }

        Util.checkClauses(createLoanRequestModel, installments, BigDecimal.valueOf(minInterestRate), BigDecimal.valueOf(maxInterestRate));

        BigDecimal loanRepayment = createLoanRequestModel.getAmount().multiply(createLoanRequestModel.getInterestRate().add(BigDecimal.ONE));
        BigDecimal installmentAmount = loanRepayment.divide(BigDecimal.valueOf(createLoanRequestModel.getNumberOfInstallment()), 2, RoundingMode.HALF_UP);
        Loan loan = createNewLoan(customer.getId(), createLoanRequestModel.getAmount(), createLoanRequestModel.getNumberOfInstallment());
        List<LoanInstallment> loanInstallmentList = new ArrayList<>();
        LocalDate localDate = LocalDate.now(zoneId).with(TemporalAdjusters.firstDayOfNextMonth());

        for (int i = 0; i < createLoanRequestModel.getNumberOfInstallment() - 1; ++i) {
            loanInstallmentList.add(
                    new LoanInstallment(
                            loan.getId(),
                            installmentAmount,
                            Timestamp.valueOf(localDate.atStartOfDay())
                    )
            );
            localDate = localDate.plusMonths(1);
        }

        // this is added because of fraction
        loanInstallmentList.add(
                new LoanInstallment(
                        loan.getId(),
                        loanRepayment.subtract(installmentAmount.multiply(BigDecimal.valueOf(createLoanRequestModel.getNumberOfInstallment() - 1))),
                        Timestamp.valueOf(localDate.atStartOfDay())
                )
        );

        loanInstallmentService.saveAll(loanInstallmentList);
        customerService.creditUsed(customer, createLoanRequestModel.getAmount());

        return new CreateLoanResponseModel(
                customer.getId(), createLoanRequestModel.getAmount(), createLoanRequestModel.getInterestRate(), loanRepayment, createLoanRequestModel.getNumberOfInstallment()
        );
    }

    private Loan createNewLoan(long customerId, BigDecimal loanAmount, int numberOfInstallment) {
        return loanRepository.save(
                new Loan(customerId, loanAmount, numberOfInstallment)
        );
    }
}
