package com.ladysparks.ttaenggrang.domain.bank.repository;

import com.ladysparks.ttaenggrang.domain.bank.dto.SavingsProductDTO;
import com.ladysparks.ttaenggrang.domain.bank.entity.SavingsPayout;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SavingsPayoutRepository extends JpaRepository<SavingsPayout, Long> {

    SavingsPayout findBySavingsSubscriptionId(Long savingsSubscriptionId);

}