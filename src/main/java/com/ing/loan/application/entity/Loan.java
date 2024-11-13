package com.ing.loan.application.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Table
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Loan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column
    private long customerId;
    @Column
    private BigDecimal loanAmount;
    @Column
    private int numberOfInstallment;
    @Column
    @CreatedDate
    private Timestamp createDate;
    @Column
    private boolean isPaid;

    public Loan(long customerId, BigDecimal loanAmount, int numberOfInstallment) {
        id = 0L;
        this.customerId = customerId;
        this.loanAmount = loanAmount;
        this.numberOfInstallment = numberOfInstallment;
        this.isPaid = false;
    }
}
