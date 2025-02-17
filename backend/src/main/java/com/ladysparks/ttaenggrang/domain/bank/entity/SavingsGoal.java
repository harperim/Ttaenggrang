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
public class SavingsGoal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private int targetAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SavingsGoalStatus status;

    @CreationTimestamp
    private Timestamp createdAt;

    @PrePersist
    public void prePersist() {
        if (this.status == null) {
            this.status = SavingsGoalStatus.IN_PROGRESS;
        }
    }

    public enum SavingsGoalStatus {
        IN_PROGRESS,
        COMPLETED
    }

}