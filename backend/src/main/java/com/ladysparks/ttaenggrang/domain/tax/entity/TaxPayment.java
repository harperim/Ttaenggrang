package com.ladysparks.ttaenggrang.domain.tax.entity;

import com.ladysparks.ttaenggrang.domain.student.entity.Student;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.CreatedDate;

import java.sql.Timestamp;
import java.time.LocalDate;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class TaxPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne
    @JoinColumn(name = "tax_id", nullable = false)
    private Tax tax;

    @Column(nullable = false)
    private int amount;

    @CreationTimestamp
    private LocalDate paymentDate;

    @Column(nullable = false)
    private boolean isOverdue;

    @CreationTimestamp
    private Timestamp createdAt;

}
