package com.ladysparks.ttaenggrang.domain.student.dto;

import com.ladysparks.ttaenggrang.domain.bank.entity.BankTransaction;
import lombok.*;

import java.sql.Timestamp;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BankTransactionSummaryDTO {

    private Long transactionId;
    private Timestamp transactionDate;
    private BankTransaction.BankTransactionType transactionType;
    private int amount;
    private int accountBalance;

}