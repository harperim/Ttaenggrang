package com.ladysparks.ttaenggrang.domain.bank.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SavingsPayout {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "savings_subscription_id", nullable = false)
    private SavingsSubscription savingsSubscription;

    @Column(nullable = false)
    private int payoutAmount;

    @Column(nullable = false)
    private int interestAmount;

    @Column(nullable = false)
    private LocalDate payoutDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SavingsPayoutType payoutType;

    @CreationTimestamp
    private Timestamp createdAt;

    public enum SavingsPayoutType {
        MATURITY, EARLY_WITHDRAWAL
    }

}
