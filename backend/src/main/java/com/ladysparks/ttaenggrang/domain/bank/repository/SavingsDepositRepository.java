package com.ladysparks.ttaenggrang.domain.bank.repository;

import com.ladysparks.ttaenggrang.domain.bank.entity.SavingsDeposit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SavingsDepositRepository extends JpaRepository<SavingsDeposit, Long> {

    List<SavingsDeposit> findBySavingsSubscriptionId(Long savingsSubscriptionId);

    @Query("SELECT s FROM SavingsDeposit s WHERE s.savingsSubscription.student.id = :studentId AND s.status = 'FAILED'")
    List<SavingsDeposit> findFailedDepositsByStudent(@Param("studentId") Long studentId);

    List<SavingsDeposit> findBySavingsSubscription_Student_Id(Long studentId);

}