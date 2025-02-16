package com.ladysparks.ttaenggrang.domain.tax.repository;

import com.ladysparks.ttaenggrang.domain.tax.entity.TaxUsage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaxUsageRepository extends JpaRepository<TaxUsage, Long> {

    List<TaxUsage> findByNationId(Long nationId);

}