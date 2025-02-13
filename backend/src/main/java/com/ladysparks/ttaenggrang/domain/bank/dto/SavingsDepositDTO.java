package com.ladysparks.ttaenggrang.domain.bank.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.ladysparks.ttaenggrang.domain.bank.entity.SavingsDeposit.SavingsDepositStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.LocalDate;

@JsonIgnoreProperties(value = {"id", "createdAt"}, allowGetters = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SavingsDepositDTO {

    private Long id;

    @NotNull(message = "적금 가입 ID(savingsSubscriptionId)는 필수 항목입니다.")
    private Long savingsSubscriptionId;

    @NotNull(message = "납입 금액(amount)은 필수 항목입니다.")
    private int amount;

    private LocalDate scheduledDate;

    @NotNull(message = "납입 상태(status)는 필수 항목입니다.")
    private SavingsDepositStatus status;

    private Timestamp createdAt;

    private Timestamp updatedAt;

}