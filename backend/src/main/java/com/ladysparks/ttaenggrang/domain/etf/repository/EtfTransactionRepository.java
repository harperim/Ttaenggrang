package com.ladysparks.ttaenggrang.domain.etf.repository;

import com.ladysparks.ttaenggrang.domain.etf.entity.EtfTransaction;
import com.ladysparks.ttaenggrang.domain.stock.entity.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Timestamp;
import java.util.List;

public interface EtfTransactionRepository extends JpaRepository<EtfTransaction, Integer>{
    // 특정 학생의 주식 거래 내역 조회
    List<EtfTransaction> findByStudentId(int studentId);
    // 특정 주식과 학생, 매수 거래를 필터링하여 거래 건수를 반환하는 메서드

    @Query("SELECT COALESCE(SUM(st.share_count), 0) FROM EtfTransaction st " +
            "WHERE st.student.id = :studentId AND st.etf.id = :etfId AND st.transactionType = :transType")
    Integer findTotalSharesByStudentAndEtf(@Param("studentId") Long studentId,
                                             @Param("etfId") int etfId,
                                             @Param("transType") TransactionType transactionType);

    @Query("SELECT COALESCE(SUM(s.share_count), 0) FROM EtfTransaction s " +
            "WHERE s.etf.id = :etfId " +
            "AND s.transactionType = :transType " +
            "AND s.trans_date BETWEEN :startDate AND :endDate")
    int getTotalSharesByType(@Param("etfId") int etfId,
                             @Param("transType") TransactionType transactionType,
                             @Param("startDate") Timestamp startDate,
                             @Param("endDate") Timestamp endDate);
}
