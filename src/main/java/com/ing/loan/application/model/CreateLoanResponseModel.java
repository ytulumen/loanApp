package com.ing.loan.application.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CreateLoanResponseModel {
    private long customerId;
    private BigDecimal amount;
    private BigDecimal interestRate;
    private BigDecimal loanRepayment;
    private int numberOfInstallment;
}