package com.ing.loan.application.service;

import com.ing.loan.application.entity.Customer;
import com.ing.loan.application.entity.User;
import com.ing.loan.application.exception.CreditLimitException;
import com.ing.loan.application.model.CreateCustomerRequestModel;
import com.ing.loan.application.model.CreateCustomerResponseModel;
import com.ing.loan.application.repository.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final UserService userService;

    public CustomerService(CustomerRepository customerRepository, UserService userService) {
        this.customerRepository = customerRepository;
        this.userService = userService;
    }

    public Customer findById(long id) {
        return customerRepository.findById(id).orElseThrow();
    }

    @Transactional
    public CreateCustomerResponseModel createNewCustomer(CreateCustomerRequestModel createCustomerRequestModel){
        Customer customer = customerRepository.save(
                new Customer(
                        createCustomerRequestModel.getName(),
                        createCustomerRequestModel.getSurname(),
                        createCustomerRequestModel.getCreditLimit(),
                        BigDecimal.ZERO
                )
        );

        userService.createNewCustomer(
                createCustomerRequestModel.getName() + "." + createCustomerRequestModel.getSurname(),
                createCustomerRequestModel.getPassword(),
                customer.getId()
        );

        return new CreateCustomerResponseModel(
                customer.getId(),
                customer.getName(),
                customer.getSurname(),
                customer.getCreditLimit()
        );
    }

    public void creditUsed(Customer customer, BigDecimal usedAmount) throws CreditLimitException {
        if (customer.getCreditLimit().compareTo(usedAmount.add(customer.getUsedCreditLimit())) < 0)
            throw new CreditLimitException(customer.getCreditLimit().subtract(customer.getUsedCreditLimit()));

        customerRepository.updateCreditLimit(customer.getId(), customer.getUsedCreditLimit().add(usedAmount));
    }

    public void creditPaidBack(Customer customer, BigDecimal creditAmount) {
        customerRepository.updateCreditLimit(customer.getId(), customer.getUsedCreditLimit().subtract(creditAmount));
    }
}
