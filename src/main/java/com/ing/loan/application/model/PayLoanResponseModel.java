package com.ing.loan.application.model;

import com.ing.loan.application.entity.LoanInstallment;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PayLoanResponseModel {
    private List<LoanInstallment> paidInstallments = new ArrayList<>();
    private BigDecimal totalAmountSpent = BigDecimal.ZERO;
    private boolean isLoanPaidCompletely = false;
    private BigDecimal change = BigDecimal.ZERO;
}
