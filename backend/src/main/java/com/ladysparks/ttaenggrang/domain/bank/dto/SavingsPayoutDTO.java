package com.ladysparks.ttaenggrang.domain.bank.dto;

import com.ladysparks.ttaenggrang.domain.bank.entity.SavingsPayout.SavingsPayoutType;
import lombok.*;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SavingsPayoutDTO {

    private Long id;
    private Long savingsSubscriptionId;
    private int payoutAmount;
    private int interestAmount;
    private LocalDate payoutDate;
    private SavingsPayoutType payoutType;
    private Timestamp createdAt;

}
