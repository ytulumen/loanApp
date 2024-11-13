package com.ing.loan.application.exception;

import java.math.BigDecimal;

public class IllegalInterestRateException extends Exception{
    public IllegalInterestRateException(BigDecimal minInterestRate, BigDecimal maxInterestRate) {
        super("Interest rate must be between: " + minInterestRate + " and " + maxInterestRate);
    }
}
