package com.ladysparks.ttaenggrang.domain.teacher.entity;

import com.ladysparks.ttaenggrang.domain.student.entity.Student;
import com.ladysparks.ttaenggrang.domain.tax.entity.TaxUsage;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Nation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nationName;

    @Column(nullable = false)
    private int population;

    @Column(nullable = false)
    private String currency;

    private int savingsGoalAmount;

    private int nationalTreasury; //국고

    @Column(nullable = false)
    private Timestamp establishedDate;

    @Column(length = 2083)
    private String profileImageUrl;  // ✅ AWS S3 이미지 URL 저장 필드

    @OneToOne
    @JoinColumn(name = "teacher_id")
    private Teacher teacher;

    @OneToOne
    @JoinColumn(name = "student_id")
    private Student student;

    @OneToMany(mappedBy = "nation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TaxUsage> taxUsages;

}
