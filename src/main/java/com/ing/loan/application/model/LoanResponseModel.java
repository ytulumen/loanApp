package com.ing.loan.application.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Timestamp;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class LoanResponseModel {
    private long customerId;
    private BigDecimal amount;
    private BigDecimal loanRepayment;
    private int numberOfInstallment;
    private Timestamp createDate;
    private boolean isPaid;
}
