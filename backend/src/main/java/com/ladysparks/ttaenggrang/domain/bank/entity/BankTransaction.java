package com.ladysparks.ttaenggrang.domain.bank.entity;

import com.ladysparks.ttaenggrang.domain.student.entity.Student;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@Getter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class BankTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "bank_account_id", nullable = false)
    private BankAccount bankAccount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BankTransactionType type;

    @Column(nullable = false)
    private int amount;

    @Column(nullable = false)
    private int balanceBefore;

    @Column(nullable = false)
    private int balanceAfter;

    @Column
    private String description;

    @ManyToOne
    @JoinColumn(name = "receiver_id")
    private Student receiver; //

    @CreationTimestamp
    private Timestamp createdAt;

    public enum BankTransactionType {
        DEPOSIT,
        WITHDRAW,
        TRANSFER,
        ITEM_BUY,
        ITEM_SELL,
        ITEM,
        STOCK_BUY,
        STOCK_SELL,
        ETF_BUY,
        ETF_SELL,
        SAVINGS_DEPOSIT,
        SAVINGS_INTEREST,
        BANK_INTEREST,
        SALARY,
        INCENTIVE,
        TAX,
        FINE,
    }


}
