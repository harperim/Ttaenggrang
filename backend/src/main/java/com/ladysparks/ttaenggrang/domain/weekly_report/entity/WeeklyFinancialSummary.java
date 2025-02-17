package com.ladysparks.ttaenggrang.domain.weekly_report.entity;

import com.ladysparks.ttaenggrang.domain.student.entity.Student;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "weekly_financial_summary")
public class WeeklyFinancialSummary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Column(nullable = false)
    private LocalDate reportDate; // 주간 기준 날짜

    @Column(nullable = false)
    private int totalIncome; // 총 수입

    @Column(nullable = false)
    private int salaryAmount; // 급여 수입

    @Column(nullable = false)
    private int savingsAmount; // 저축액

    @Column(nullable = false)
    private int investmentReturn; // 투자 수익

    @Column(nullable = false)
    private int incentiveAmount; // 인센티브

    @Column(nullable = false)
    private int totalExpenses; // 총 지출

    @Column(nullable = false)
    private int taxAmount; // 세금 납부액

    @Column(nullable = false)
    private int fineAmount; // 벌금 납부액

    @Column
    private String aiFeedback; // AI 피드백
    
    @CreationTimestamp
    private Timestamp createdAt;
    
}
