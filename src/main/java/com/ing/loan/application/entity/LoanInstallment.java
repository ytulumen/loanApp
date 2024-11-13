package com.ing.loan.application.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Table
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class LoanInstallment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column
    private long loanId;
    @Column
    private BigDecimal amount;
    @Column
    private BigDecimal paidAmount;
    @Column
    private Timestamp dueDate;
    @Column
    private Timestamp paymentDate = null;
    @Column
    private boolean isPaid;

    public LoanInstallment(long loanId, BigDecimal amount, Timestamp dueDate) {
        id = 0L;
        this.loanId = loanId;
        this.amount = amount;
        this.paidAmount = BigDecimal.ZERO;
        this.dueDate = dueDate;
        this.paymentDate = null;
        isPaid = false;
    }
}
