package com.ladysparks.ttaenggrang.domain.teacher.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.naming.Name;
import java.sql.Timestamp;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Nation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String nationName;

    @Column(nullable = false)
    private Integer population;

    @Column(nullable = false)
    private String currency;

    @Column(nullable = false)
    private Integer savingsGoalAmount;

    private int nationalTreasury;

    @Column(nullable = false)
    private Timestamp establishedDate;

    @Column(length = 2083)
    private String profileImageUrl;  // ✅ AWS S3 이미지 URL 저장 필드

    @OneToOne
    @JoinColumn(name = "teacher_id")
    private Teacher teacher;
}
