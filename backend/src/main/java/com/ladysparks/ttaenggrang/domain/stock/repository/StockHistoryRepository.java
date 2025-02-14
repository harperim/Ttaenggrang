package com.ladysparks.ttaenggrang.domain.stock.repository;

import com.ladysparks.ttaenggrang.domain.stock.entity.StockHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public interface StockHistoryRepository extends JpaRepository<StockHistory, Long> {

    // 매수량 합계 조회 (BUY 거래만 필터링)
    @Query("SELECT SUM(sh.buyVolume) FROM StockHistory sh WHERE sh.stock.id = :stockId AND sh.date BETWEEN :startTime AND :endTime")
    int getTotalBuyVolumeInRange(@Param("stockId") Long stockId, @Param("startTime") LocalTime startTime, @Param("endTime") LocalTime endTime);

    // 특정 날짜 및 시간 범위 내의 판매량 합계
    @Query("SELECT SUM(sh.sellVolume) FROM StockHistory sh WHERE sh.stock.id = :stockId AND sh.date BETWEEN :startTime AND :endTime")
    int getTotalSellVolumeInRange(@Param("stockId") Long stockId, @Param("startTime") LocalTime startTime, @Param("endTime") LocalTime endTime);


    //StockHistory 조회
    List<StockHistory> findByStockId(Long stockId);



}

