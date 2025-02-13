package com.ladysparks.ttaenggrang.domain.teacher.entity;

import com.ladysparks.ttaenggrang.domain.student.entity.Student;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Data
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String jobName;

    @Column(nullable = false)
    private String jobDescription;

    @Column(nullable = false)
    private Integer baseSalary;

    @Column(nullable = false)
    private Integer maxPeople;

    @Column(nullable = false)
    private Timestamp salaryDate;

    @PrePersist
    protected void onCreate() {
        this.salaryDate = new Timestamp(System.currentTimeMillis());  // 기본값 설정
    }

    @OneToMany(mappedBy = "job", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Student> students = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id")
    private Teacher teacher;
}
