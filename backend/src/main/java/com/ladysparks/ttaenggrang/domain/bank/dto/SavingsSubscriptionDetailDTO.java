package com.ladysparks.ttaenggrang.domain.bank.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SavingsSubscriptionDetailDTO {

    private String savingsName;
    private LocalDate startDate;
    private LocalDate endDate;
    private List<SavingsDepositHistoryDTO> depositHistory;
    private int payoutAmount;

}