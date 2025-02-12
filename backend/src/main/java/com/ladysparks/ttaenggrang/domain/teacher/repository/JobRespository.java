package com.ladysparks.ttaenggrang.domain.teacher.repository;

import com.ladysparks.ttaenggrang.domain.teacher.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface JobRespository extends JpaRepository<Job, Long> {
    Optional<Job> findByJobName(String jobName);

    // 교사 ID로 우리 반 직업 목록 조회
    List<Job> findAllByTeacherId(Long teacherId);
}
