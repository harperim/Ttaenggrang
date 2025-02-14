package com.ladysparks.ttaenggrang.domain.bank.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.ladysparks.ttaenggrang.domain.bank.entity.SavingsSubscription.SavingsSubscriptionStatus;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

@JsonIgnoreProperties(value = {"id", "studentId", "durationWeeks", "interestRate", "amount", "startDate", "endDate", "payoutAmount", "status", "createdAt", "depositSchedule"}, allowGetters = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class SavingsSubscriptionDTO {

    private Long id;

    @NotNull(message = "적금 상품 ID(savingsProductId)는 필수 항목입니다.")
    private Long savingsProductId;

    private Long studentId;

    private int durationWeeks;

    private float interestRate;

    private int amount;

    private LocalDate startDate;

    private LocalDate endDate;

    private int payoutAmount;

    private SavingsSubscriptionStatus status;

    @NotNull(message = "납입 요일(depositDayOfWeek)는 필수 항목입니다.")
    private DayOfWeek depositDayOfWeek;

    private Timestamp createdAt;

//    private List<LocalDate> depositSchedule; // 자동 납입 일정

}