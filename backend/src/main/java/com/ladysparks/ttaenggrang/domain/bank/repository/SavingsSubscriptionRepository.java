package com.ladysparks.ttaenggrang.domain.bank.repository;

import com.ladysparks.ttaenggrang.domain.bank.entity.SavingsSubscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface SavingsSubscriptionRepository extends JpaRepository<SavingsSubscription, Long> {

    List<SavingsSubscription> findByStudentId(Long studentId);

    List<SavingsSubscription> findBySavingsProductId(Long savingsProductId);

    boolean existsByStudentIdAndSavingsProductId(Long studentId, Long savingsProductId);

    long countByStudentId(Long studentId);

    @Query("SELECT COALESCE(SUM(sd.amount), 0) FROM SavingsDeposit sd WHERE sd.savingsSubscription.id = :subscriptionId AND sd.status = 'COMPLETED'")
    int findTotalDepositedAmount(@Param("subscriptionId") Long subscriptionId);

    @Query("SELECT s.savingsProduct.name FROM SavingsSubscription s WHERE s.id = :savingsSubscriptionId")
    String findSavingsProductNameBySubscriptionId(@Param("savingsSubscriptionId") Long savingsSubscriptionId);

    List<SavingsSubscription> findAllByEndDateAndStatus(LocalDate now, SavingsSubscription.SavingsSubscriptionStatus savingsSubscriptionStatus);

    List<SavingsSubscription> findAllByStatus(SavingsSubscription.SavingsSubscriptionStatus savingsSubscriptionStatus);

}
