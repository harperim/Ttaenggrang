package com.ladysparks.ttaenggrang.domain.stock.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.ladysparks.ttaenggrang.domain.teacher.entity.Teacher;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Entity
public class StockMarketStatus {

    @Id
    private Long teacherId;  // 외래키 + 기본키

    @OneToOne
    @JsonIgnore
    @MapsId  // teacherId를 외래키이자 기본키로 사용
    @JoinColumn(name = "teacher_id")
    private Teacher teacher;

    @Column(name = "is_market_open")
    private boolean isMarketOpen;   // 현재 시장 활성화 상태

    @Column(name = "is_teacher_on")
    private boolean isTeacherOn;    // 선생님 버튼으로 수동 설정 여부

}
