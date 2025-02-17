package com.ladysparks.ttaenggrang.domain.bank.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
public class StudentDailyAverageFinancialDTO  {

    private LocalDate date;       // 날짜
    private double averageIncome;  // 평균 수입
    private double averageExpense; // 평균 지출

}