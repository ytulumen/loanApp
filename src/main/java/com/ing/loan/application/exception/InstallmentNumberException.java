package com.ing.loan.application.exception;

public class InstallmentNumberException extends Exception{
    public InstallmentNumberException(String installments) {
        super("Installments must be: " + installments);
    }
}
