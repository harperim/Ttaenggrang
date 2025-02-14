package com.ladysparks.ttaenggrang.domain.stock.repository;

import com.ladysparks.ttaenggrang.domain.stock.entity.StockTransaction;
import com.ladysparks.ttaenggrang.domain.stock.entity.TransType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public interface StockTransactionRepository extends JpaRepository<StockTransaction, Long> {
    // 특정 학생의 주식 거래 내역 조회
    List<StockTransaction> findByStudentId(int studentId);
    // 특정 주식과 학생, 매수 거래를 필터링하여 거래 건수를 반환하는 메서드


    //학생 매수,매도 조회
    @Query("SELECT COALESCE(SUM(st.share_count), 0) FROM StockTransaction st " +
            "WHERE st.student.id = :studentId AND st.stock.id = :stockId AND st.transType = :transType")
    Integer findTotalSharesByStudentAndStock(@Param("studentId") Long studentId,
                                             @Param("stockId") int stockId,
                                             @Param("transType") TransType transType);



//    @Query("SELECT COALESCE(SUM(s.share_count), 0) FROM StockTransaction s " +
//            "WHERE s.stock.id = :stockId " +
//            "AND s.transType = :transType " +
//            "AND s.trans_date BETWEEN :startDate AND :endDate")
//    int getTotalSharesByType(@Param("stockId") int stockId,
//                             @Param("transType") TransType transType,
//                             @Param("startDate") Timestamp startDate,
//                             @Param("endDate") Timestamp endDate);
//


    // 특정 주식에 대한 매수량을 조회하는 쿼리
    @Query("SELECT COALESCE(SUM(s.share_count), 0) FROM StockTransaction s " +
            "WHERE s.stock.id = :stockId " +
            "AND s.transType = :transType " +
            "AND s.trans_date BETWEEN :startTime AND :endTime")
    int getBuyVolumeForStockInTimeRange(
            @Param("stockId") Long stockId,
            @Param("transType") TransType transType,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);

    // 특정 주식에 대한 매도량을 조회하는 쿼리
    @Query("SELECT COALESCE(SUM(s.share_count), 0) FROM StockTransaction s " +
            "WHERE s.stock.id = :stockId " +
            "AND s.transType = :transType " +
            "AND s.trans_date BETWEEN :startTime AND :endTime")
    int getSellVolumeForStockInTimeRange(
            @Param("stockId") Long stockId,
            @Param("transType") TransType transType,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);


}