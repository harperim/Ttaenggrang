package com.ladysparks.ttaenggrang.domain.weekly_report.repository;

import com.ladysparks.ttaenggrang.domain.weekly_report.entity.WeeklyFinancialSummary;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface WeeklyFinancialSummaryRepository extends JpaRepository<WeeklyFinancialSummary, Long> {

    Optional<WeeklyFinancialSummary> findByStudentIdAndReportDate(Long studentId, LocalDate reportDate);

    @Query("SELECT w FROM WeeklyFinancialSummary w WHERE w.student.id = :studentId ORDER BY w.reportDate DESC")
    List<WeeklyFinancialSummary> findRecentReportsByStudentId(@Param("studentId") Long studentId);

    /**
     * 특정 학생의 최근 N개의 금융 리포트 조회 (최신순 정렬)
     */
    @Query("SELECT w FROM WeeklyFinancialSummary w WHERE w.student.id = :studentId ORDER BY w.reportDate DESC")
    List<WeeklyFinancialSummary> findRecentReportsByStudentId(@Param("studentId") Long studentId, Pageable pageable);

    /**
     * 📌 특정 학생의 최신 주간 AI 피드백 조회
     */
    @Query("""
        SELECT w.aiFeedback FROM WeeklyFinancialSummary w
        WHERE w.student.id = :studentId
        ORDER BY w.reportDate DESC
    """)
    List<String> findLatestAIFeedbackByStudentId(@Param("studentId") Long studentId, Pageable pageable);

}
