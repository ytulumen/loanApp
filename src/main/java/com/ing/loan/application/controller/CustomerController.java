package com.ing.loan.application.controller;

import com.ing.loan.application.model.CreateCustomerRequestModel;
import com.ing.loan.application.model.CreateCustomerResponseModel;
import com.ing.loan.application.service.CustomerService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/customer")
public class CustomerController {
    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public CreateCustomerResponseModel createCustomer(
            @RequestBody CreateCustomerRequestModel body) {
        return customerService.createNewCustomer(body);
    }
}
