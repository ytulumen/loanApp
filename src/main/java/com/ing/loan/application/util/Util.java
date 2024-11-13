package com.ing.loan.application.util;

import com.ing.loan.application.exception.IllegalInterestRateException;
import com.ing.loan.application.exception.InstallmentNumberException;
import com.ing.loan.application.model.CreateLoanRequestModel;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

public class Util {
    public static void checkClauses(CreateLoanRequestModel createLoanRequestModel, String installments, BigDecimal minInterestRate, BigDecimal maxInterestRate) throws InstallmentNumberException, IllegalInterestRateException {
        List<Integer> installmentsList = Arrays.stream(installments.split(",")).map(Integer::valueOf).toList();

        if (!installmentsList.contains(createLoanRequestModel.getNumberOfInstallment())){
            throw new InstallmentNumberException(installments);
        }

        if (createLoanRequestModel.getInterestRate().compareTo(maxInterestRate) > 0 || createLoanRequestModel.getInterestRate().compareTo(minInterestRate) < 0)
            throw new IllegalInterestRateException(minInterestRate, maxInterestRate);
    }
}
