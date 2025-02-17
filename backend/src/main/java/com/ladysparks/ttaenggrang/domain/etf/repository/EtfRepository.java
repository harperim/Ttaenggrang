package com.ladysparks.ttaenggrang.domain.etf.repository;

import com.ladysparks.ttaenggrang.domain.etf.entity.Etf;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

public interface EtfRepository extends JpaRepository<Etf, Long>{
    boolean existsByName(String name);

    // 이번주 ETF 평가액 조회
    // SUM(보유량 * 현재 ETF 가격)
    @Query("SELECT COALESCE(SUM(et.owned_qty * e.price_per), 0) FROM EtfTransaction et " +
            "JOIN Etf e ON et.etf.id = e.id " +
            "WHERE et.student.id = :studentId")
    int getCurrentEtfEvaluation(@Param("studentId") Long studentId);


    // 지난주 ETF 평가액 조회
    @Query("SELECT COALESCE(SUM(et.owned_qty * e.price_per), 0) FROM EtfTransaction et " +
            "JOIN Etf e ON et.etf.id = e.id " +
            "WHERE et.student.id = :studentId " +
            "AND et.trans_date BETWEEN :startDate AND :endDate")
    int getLastWeekEtfEvaluation(@Param("studentId") Long studentId,
                                 @Param("startDate") LocalDateTime startDate,
                                 @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COALESCE(SUM(e.price_per * et.owned_qty), 0) FROM Etf e " +
            "JOIN EtfTransaction et ON e.id = et.etf.id " +
            "WHERE et.student.id = :studentId AND et.trans_date BETWEEN :startDate AND :endDate")
    Optional<Integer> getEtfEvaluationByDate(@Param("studentId") Long studentId,
                                             @Param("startDate") LocalDateTime startDate,
                                             @Param("endDate") LocalDateTime endDate);

}