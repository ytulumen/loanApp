package com.ing.loan.application.controller;

import com.ing.loan.application.entity.Customer;
import com.ing.loan.application.entity.LoanInstallment;
import com.ing.loan.application.exception.CreditLimitException;
import com.ing.loan.application.exception.IllegalInterestRateException;
import com.ing.loan.application.exception.InstallmentNumberException;
import com.ing.loan.application.exception.UnauthorizedException;
import com.ing.loan.application.model.*;
import com.ing.loan.application.service.CustomerService;
import com.ing.loan.application.service.LoanService;
import com.ing.loan.application.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("loan")
public class LoanController {
    private final UserService userService;
    private final CustomerService customerService;

    private final LoanService loanService;

    public LoanController(UserService userService, CustomerService customerService, LoanService loanService) {
        this.userService = userService;
        this.customerService = customerService;
        this.loanService = loanService;
    }

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    public CreateLoanResponseModel create(@RequestBody CreateLoanRequestModel body, Principal principal) throws UnauthorizedException, IllegalInterestRateException, InstallmentNumberException, CreditLimitException {
        checkCredential(body.getCustomerId(), principal);
        return loanService.create(body);
    }

    @GetMapping("/list/{customerId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    public List<LoanResponseModel> getLoans(@PathVariable long customerId, Principal principal) throws UnauthorizedException {
        checkCredential(customerId, principal);
        return loanService.listLoans(customerId);
    }

    @GetMapping("/installments/{customerId}/{loanId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    public List<LoanInstallment> getLoanInstallments(@PathVariable long customerId, @PathVariable long loanId, Principal principal) throws UnauthorizedException {
        checkCredential(customerId, principal);
        return loanService.getLoanInstallments(customerId, loanId, principal.getName());
    }

    @PostMapping("/pay")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    public PayLoanResponseModel payLoan(@RequestBody PayLoanRequestModel body, Principal principal) throws UnauthorizedException {
        // checkCredential(customerId, principal);
        return loanService.payLoan(body);
    }

    private void checkCredential(long customerId, Principal principal) throws UnauthorizedException {
        Customer customer = customerService.findById(customerId);
        String username = customer.getName() + "." + customer.getSurname();

        if (!principal.getName().equals(username) && !userService.isUserAdmin(principal.getName())) {
            throw new UnauthorizedException();
        }

    }
}
