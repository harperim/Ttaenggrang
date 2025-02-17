package com.ladysparks.ttaenggrang.domain.stock.repository;

import com.ladysparks.ttaenggrang.domain.stock.entity.StockHistory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public interface StockHistoryRepository extends JpaRepository<StockHistory, Long> {

    // 매수량 합계 조회 (BUY 거래만 필터링)
    @Query("SELECT SUM(sh.buyVolume) FROM StockHistory sh WHERE sh.stock.id = :stockId AND sh.createdAt BETWEEN :startTime AND :endTime")
    int getTotalBuyVolumeInRange(@Param("stockId") Long stockId, @Param("startTime") LocalTime startTime, @Param("endTime") LocalTime endTime);

    // 특정 날짜 및 시간 범위 내의 판매량 합계
    @Query("SELECT SUM(sh.sellVolume) FROM StockHistory sh WHERE sh.stock.id = :stockId AND sh.createdAt BETWEEN :startTime AND :endTime")
    int getTotalSellVolumeInRange(@Param("stockId") Long stockId, @Param("startTime") LocalTime startTime, @Param("endTime") LocalTime endTime);

    // StockHistory 조회
    List<StockHistory> findByStockId(Long stockId);

    /**
     * 특정 주식의 가장 최근 StockHistory 조회 (어제 기록된 변동 내역)
     */
    @Query("SELECT sh FROM StockHistory sh WHERE sh.stock.id = :stockId ORDER BY sh.createdAt DESC LIMIT 1")
    StockHistory findLatestHistoryByStockId(@Param("stockId") Long stockId);

    /**
     * 📌 특정 주식의 최근 5일치 변동 이력 조회 (가격 변동률 포함, 오래된 순서로 정렬)
     */
    @Query("SELECT sh FROM StockHistory sh WHERE sh.stock.id = :stockId AND sh.createdAt BETWEEN :startDate AND :endDate ORDER BY sh.createdAt ASC")
    List<StockHistory> findLast5DaysByStockId(@Param("stockId") Long stockId,
                                              @Param("startDate") LocalDateTime startDate,
                                              @Param("endDate") LocalDateTime endDate);

    /**
     * 📌 특정 주식의 최근 5개의 평일 변동 이력 조회 (가격 변동률 포함, 오래된 순서로 정렬)
     * 📌 주말(일요일: 1, 토요일: 7) 제외
     */
    @Query("""
        SELECT sh FROM StockHistory sh
        WHERE sh.stock.id = :stockId
        AND FUNCTION('DAYOFWEEK', sh.createdAt) NOT IN (1, 7)
        ORDER BY sh.createdAt ASC
    """)
    List<StockHistory> findLast5WeekdaysByStockId(@Param("stockId") Long stockId, Pageable pageable);

}

