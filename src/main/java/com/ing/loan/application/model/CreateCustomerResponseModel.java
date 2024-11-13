package com.ing.loan.application.model;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CreateCustomerResponseModel {
    private long id;
    private String name;
    private String surname;
    private BigDecimal creditLimit;
}
