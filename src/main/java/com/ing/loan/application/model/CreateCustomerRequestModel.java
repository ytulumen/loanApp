package com.ing.loan.application.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CreateCustomerRequestModel {
    private String name;
    private String surname;
    private BigDecimal creditLimit;
    private String password;
}
