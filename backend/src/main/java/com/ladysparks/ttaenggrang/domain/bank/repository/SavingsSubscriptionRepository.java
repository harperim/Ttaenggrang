package com.ladysparks.ttaenggrang.domain.bank.repository;

import com.ladysparks.ttaenggrang.domain.bank.entity.SavingsSubscription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SavingsSubscriptionRepository extends JpaRepository<SavingsSubscription, Long> {

    List<SavingsSubscription> findByStudentId(Long studentId);

    List<SavingsSubscription> findBySavingsProductId(Long savingsProductId);

    boolean existsByStudentIdAndSavingsProductId(Long studentId, Long savingsProductId);

    long countByStudentId(Long studentId);

}
