package com.ladysparks.ttaenggrang.domain.teacher.repository;

import com.ladysparks.ttaenggrang.domain.student.entity.Student;
import com.ladysparks.ttaenggrang.domain.teacher.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface JobRespository extends JpaRepository<Job, Long> {
    Optional<Job> findByJobName(String jobName);

    // 교사 ID로 우리 반 직업 목록 조회
    List<Job> findAllByTeacherId(Long teacherId);

    @Query("SELECT j.baseSalary FROM Job j WHERE j.id = :jobId")
    int findBaseSalaryById(Long jobId);

    Optional<Job> findByJobNameAndTeacherId(String jobName, Long teacherId);

    Optional<Job> findByStudents_Id(Long studentId);

    Optional<Job> findById(Long jobId);  // job의 id로 학생 찾기
}
