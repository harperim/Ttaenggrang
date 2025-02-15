package com.ladysparks.ttaenggrang.domain.teacher.repository;

import com.ladysparks.ttaenggrang.domain.teacher.entity.Nation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface NationRepository extends JpaRepository<Nation, Long> {

    // 교사 ID로 국가 조회
    Optional<Nation> findByTeacher_Id(Long teacherId);

    boolean existsByTeacherId(Long teacherId);

    @Query("SELECT n.savingsGoalAmount FROM Nation n WHERE n.teacher.id = :teacherId")
    Optional<Integer> findSavingsGoalAmountByTeacher_Id(@Param("teacherId") Long teacherId);

    // 학생 ID로 국가 조회
    @Query("SELECT n FROM Nation n WHERE n.student.id = :studentId")
    Optional<Nation> findNationByStudentId(@Param("studentId") Long studentId);

    @Query("SELECT n FROM Nation n WHERE n.teacher.id = :teacherId")
    Optional<Nation> findNationByTeacherId(@Param("teacherId") Long teacherId);


}
