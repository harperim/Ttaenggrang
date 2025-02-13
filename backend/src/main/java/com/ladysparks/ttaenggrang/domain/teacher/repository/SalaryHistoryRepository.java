package com.ladysparks.ttaenggrang.domain.teacher.repository;

import com.ladysparks.ttaenggrang.domain.teacher.entity.SalaryHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SalaryHistoryRepository extends JpaRepository<SalaryHistory, Long> {

    // 가장 최근 급여 지급 기록 조회
    Optional<SalaryHistory> findTopByTeacherIdOrderByDistributedAtDesc(Long teacherId);

}
