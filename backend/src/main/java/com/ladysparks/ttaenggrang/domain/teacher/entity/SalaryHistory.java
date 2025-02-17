package com.ladysparks.ttaenggrang.domain.teacher.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ladysparks.ttaenggrang.domain.student.entity.Student;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Service
@Builder
public class SalaryHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "student_id")
    private Student student;

    private Integer amountPaid;  // 지급된 금액
    private Timestamp distributedAt;  // 지급된 날짜

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "teacher_id")
    @JsonIgnore
    private Teacher teacher;

    @PrePersist
    protected void onCreate() {
        this.distributedAt = new Timestamp(System.currentTimeMillis());
    }
}
