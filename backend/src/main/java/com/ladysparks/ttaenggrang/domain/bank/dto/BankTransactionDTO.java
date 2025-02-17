package com.ladysparks.ttaenggrang.domain.bank.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.ladysparks.ttaenggrang.domain.bank.entity.BankTransaction.BankTransactionType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@JsonIgnoreProperties(value = {"id", "bankAccountId", "balanceBefore", "balanceAfter", "createdAt"}, allowGetters = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class BankTransactionDTO {

    private Long id;

    private Long bankAccountId;

    @NotNull(message = "거래 타입(type)은 필수 항목입니다.")
    private BankTransactionType type;

    @NotNull(message = "거래 금액(amount)은 필수 항목입니다.")
    @Min(value = 1, message = "거래 금액(amount)은 1 이상이어야 합니다.")
    private int amount;

    private int balanceBefore;
    private int balanceAfter;

    @NotNull(message = "거래 내용(description)은 필수 항목입니다.")
    private String description;

    private Long receiverId;

    private Timestamp createdAt;

}
