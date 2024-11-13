package com.ing.loan.application.repository;


import com.ing.loan.application.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    @Query("UPDATE Customer c SET c.usedCreditLimit = :newLimit WHERE c.id = :id")
    @Modifying
    void updateCreditLimit(long id, BigDecimal newLimit);
}
