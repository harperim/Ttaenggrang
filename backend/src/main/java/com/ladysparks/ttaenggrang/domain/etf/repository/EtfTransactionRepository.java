package com.ladysparks.ttaenggrang.domain.etf.repository;

import com.ladysparks.ttaenggrang.domain.etf.entity.EtfTransaction;
import com.ladysparks.ttaenggrang.domain.etf.entity.TransType;
import com.ladysparks.ttaenggrang.domain.stock.entity.StockTransaction;
import com.ladysparks.ttaenggrang.domain.stock.entity.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

public interface EtfTransactionRepository extends JpaRepository<EtfTransaction, Long>{

    // 특정 학생의 주식 거래 내역 조회
    List<EtfTransaction> findByStudentId(Long studentId);
    // 특정 주식과 학생, 매수 거래를 필터링하여 거래 건수를 반환하는 메서드


    //학생 매수,매도 조회
    @Query("SELECT COALESCE(SUM(st.share_count), 0) FROM EtfTransaction st " +
            "WHERE st.student.id = :studentId AND st.etf.id = :etfId AND st.transType = :transType")
    Integer findTotalSharesByStudentAndEtf(@Param("studentId") Long studentId,
                                             @Param("etfId") Long etfId,
                                             @Param("transType") TransType transType);

    // 특정 주식에 대한 매수량을 조회하는 쿼리
    @Query("SELECT COALESCE(SUM(s.share_count), 0) FROM EtfTransaction s " +
            "WHERE s.etf.id = :etfId " +
            "AND s.transType = :transType " +
            "AND s.transDate BETWEEN :startTime AND :endTime")
    int getBuyVolumeForEtfInTimeRange(
            @Param("etfId") Long etfId,
            @Param("transType") TransType transType,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);

    // 특정 주식에 대한 매도량을 조회하는 쿼리
    @Query("SELECT COALESCE(SUM(s.share_count), 0) FROM EtfTransaction s " +
            "WHERE s.etf.id = :etfId " +
            "AND s.transType = :transType " +
            "AND s.transDate BETWEEN :startTime AND :endTime")
    int getSellVolumeForEtfInTimeRange(
            @Param("etfId") Long etfId,
            @Param("transType") TransactionType transactionType,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);

    @Query("SELECT COUNT(s) FROM EtfTransaction s GROUP BY s.etf.id")
    List<Integer> findAllTransactionVolumes();

    int countByEtfIdAndTransDateAfter(Long etf_id, Timestamp transDate);

    // 추가
    /**
     * 특정 주식의 당일 총 매수량 조회
     */
    @Query("SELECT COALESCE(SUM(s.share_count), 0) FROM EtfTransaction s " +
            "WHERE s.etf.id = :etfId " +
            "AND s.transType = 'BUY' " +
            "AND s.transDate BETWEEN :startOfDay AND :endOfDay")
    int getTotalBuyVolume(@Param("etfId") Long etfId,
                          @Param("startOfDay") LocalDateTime startOfDay,
                          @Param("endOfDay") LocalDateTime endOfDay);

    /**
     * 특정 주식의 당일 총 매도량 조회
     */
    @Query("SELECT COALESCE(SUM(s.share_count), 0) FROM EtfTransaction s " +
            "WHERE s.etf.id = :etfId " +
            "AND s.transType = 'SELL' " +
            "AND s.transDate BETWEEN :startOfDay AND :endOfDay")
    int getTotalSellVolume(@Param("etfId") Long etfId,
                           @Param("startOfDay") LocalDateTime startOfDay,
                           @Param("endOfDay") LocalDateTime endOfDay);

}
