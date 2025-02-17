package com.ladysparks.ttaenggrang.domain.weekly_report.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class WeeklyReportDTO {

    private int totalIncome;       // 총 수입
    private int salaryAmount;      // 급여액
    private int savingsAmount;     // 저축액
    private int investmentReturn;  // 투자 수익
    private int incentiveAmount;   // 인센티브
    private int totalExpenses;     // 소비액
    private int taxAmount;         // 세금 납부액
    private int fineAmount;        // 벌금 납부액

}
