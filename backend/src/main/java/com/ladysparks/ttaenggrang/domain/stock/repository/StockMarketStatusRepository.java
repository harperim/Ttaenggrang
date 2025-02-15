package com.ladysparks.ttaenggrang.domain.stock.repository;

import com.ladysparks.ttaenggrang.domain.stock.entity.StockMarketStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface StockMarketStatusRepository extends JpaRepository<StockMarketStatus, Long> {

    @Query("SELECT s.isMarketOpen FROM StockMarketStatus s WHERE s.teacher.id = :teacherId")
    Boolean findIsMarketOpenByTeacherId(Long teacherId);

}
