package com.ladysparks.ttaenggrang.domain.bank.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;
import java.time.LocalDate;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class SavingsDeposit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "savings_subscription_id", nullable = false)
    private SavingsSubscription savingsSubscription;

    @Column(nullable = false)
    private int amount;

    @Column
    private int balance;

    @Column(nullable = false)
    private LocalDate scheduledDate; // 예정된 납입 날짜

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SavingsDepositStatus status;

    @CreationTimestamp
    private Timestamp createdAt;

    @UpdateTimestamp
    private Timestamp updatedAt;

    public void updateAmount(int amount) {
        this.amount = amount;
    }

    public void updateStatus(SavingsDepositStatus status) {
        this.status = status;
        this.updatedAt = new Timestamp(System.currentTimeMillis());
    }

    public enum SavingsDepositStatus {
        PENDING,     // 예정됨 (예정일 전)
        COMPLETED,   // 납입 완료
        FAILED       // 미납 (잔액 부족)
    }

}
