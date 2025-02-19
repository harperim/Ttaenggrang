package com.ladysparks.ttaenggrang.domain.etf.repository;

import com.ladysparks.ttaenggrang.domain.etf.entity.EtfHistory;
import com.ladysparks.ttaenggrang.domain.stock.entity.StockHistory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface EtfHistoryRepository extends JpaRepository <EtfHistory, Long>{

    List<EtfHistory> findLast5DaysByTeacherId(Long teacherId);
    @Query("""
    SELECT sh FROM EtfHistory sh
    WHERE sh.etf.id = :etfId
    AND FUNCTION('DAYOFWEEK', sh.createdAt) NOT IN (1, 7)
    ORDER BY sh.createdAt DESC
""")
    List<EtfHistory> findLast5WeekdaysIncludingToday(@Param("etfId") Long etfId, Pageable pageable);

    List<EtfHistory> findByEtfId(Long etfId);
}
