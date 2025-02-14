package com.ladysparks.ttaenggrang.domain.tax.repository;

import com.ladysparks.ttaenggrang.domain.tax.entity.TaxInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaxInfoRepository extends JpaRepository<TaxInfo, Long> {
}