package com.ladysparks.ttaenggrang.domain.vote.entity;

import com.ladysparks.ttaenggrang.domain.teacher.entity.Teacher;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Vote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private Timestamp startDate;
    private Timestamp endDate;

    @Enumerated(EnumType.STRING)
    private VoteMode voteMode;  // 투표 모드 (STUDENT / CUSTOM)

    @Enumerated(EnumType.STRING)
    private VoteStatus status;  // 투표 상태 (ACTIVE / INACTIVE)

    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private Teacher teacher;
}
