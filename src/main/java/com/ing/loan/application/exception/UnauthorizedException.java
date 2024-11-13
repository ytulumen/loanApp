package com.ing.loan.application.exception;

public class UnauthorizedException extends Exception {
    public UnauthorizedException() {
        super("Loan applicant customer id and credential is not matching!");
    }
}
