package com.ing.loan.application.exception;

import java.math.BigDecimal;

public class CreditLimitException extends Exception{
    public CreditLimitException(BigDecimal maxLimit) {
        super("Customer has no limit for requested amount. Max amount is: " + maxLimit);
    }
}
