package com.ladysparks.ttaenggrang.domain.teacher.repository;

import com.ladysparks.ttaenggrang.domain.teacher.entity.Incentive;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IncentiveRepository extends JpaRepository<Incentive, Long> {

    // 특정 학생의 가장 최근 인센티브 지급 기록 조회
    Optional<Incentive> findTopByStudentIdOrderByCreatedAtDesc(Long studentId);
}
