package com.ladysparks.ttaenggrang.domain.tax.repository;

import com.ladysparks.ttaenggrang.domain.tax.entity.Tax;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TaxRepository  extends JpaRepository<Tax, Long> {

    Optional<Tax> findByTaxName(String taxName);

    Optional<Tax> findByTaxNameAndTeacherId(String taxName, Long teacherId);

    List<Tax> findByTeacherId(Long teacherId);

}
