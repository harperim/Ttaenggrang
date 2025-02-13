package com.ladysparks.ttaenggrang.domain.bank.entity;

import com.ladysparks.ttaenggrang.domain.student.entity.Student;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;
import java.time.DayOfWeek;
import java.time.LocalDate;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class SavingsSubscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "savings_product_id", nullable = false)
    private SavingsProduct savingsProduct;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SavingsSubscriptionStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DayOfWeek depositDayOfWeek;

    @CreationTimestamp
    private Timestamp createdAt;

    @PrePersist
    public void prePersist() {
        if (this.status == null) {
            this.status = SavingsSubscriptionStatus.ACTIVE;
        }
    }

    public enum SavingsSubscriptionStatus {
        ACTIVE, WITHDRAWN, MATURED
    }

}
