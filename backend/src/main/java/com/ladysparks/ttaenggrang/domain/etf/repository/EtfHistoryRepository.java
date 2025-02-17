package com.ladysparks.ttaenggrang.domain.etf.repository;

import com.ladysparks.ttaenggrang.domain.etf.entity.EtfHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EtfHistoryRepository extends JpaRepository <EtfHistory, Long>{
}
