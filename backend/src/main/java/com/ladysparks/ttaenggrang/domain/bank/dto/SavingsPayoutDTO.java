package com.ladysparks.ttaenggrang.domain.bank.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ladysparks.ttaenggrang.domain.bank.entity.SavingsPayout.SavingsPayoutType;
import lombok.*;

import java.sql.Timestamp;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SavingsPayoutDTO {

    private Long id;

    private Long savingsSubscriptionId;

    private int principalAmount;

    private int interestAmount;

    private int payoutAmount;

    private LocalDate payoutDate;

    private SavingsPayoutType payoutType;

    @JsonProperty("isPaid")
    private boolean isPaid;

    private Timestamp createdAt;

}
