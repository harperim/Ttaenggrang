package com.ladysparks.ttaenggrang.domain.stock.repository;

import com.ladysparks.ttaenggrang.domain.etf.entity.Etf;
import com.ladysparks.ttaenggrang.domain.stock.entity.MarketStatus;
import com.ladysparks.ttaenggrang.domain.stock.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MarketStatusRepository extends JpaRepository<MarketStatus, Long> {
    Optional<MarketStatus> findFirstByIsManualOverrideTrue();
    Optional<MarketStatus> findByStock(Stock stock);


    Optional<MarketStatus> findByEtf(Etf etf);
}
