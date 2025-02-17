package com.ladysparks.ttaenggrang.domain.bank.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SavingsDepositHistoryDTO {

    private Long id;
    private LocalDate transactionDate;
    private String transactionType;
    private int amount;
    private float interestRate;
    private int balance;
//    private LocalDate startDate;
//    private LocalDate maturityDate;

}