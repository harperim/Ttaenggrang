package com.ladysparks.ttaenggrang.domain.vote.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ladysparks.ttaenggrang.domain.teacher.entity.Teacher;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

@Entity
@Getter
@Setter
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
    @JsonIgnore
    private Teacher teacher;
}
