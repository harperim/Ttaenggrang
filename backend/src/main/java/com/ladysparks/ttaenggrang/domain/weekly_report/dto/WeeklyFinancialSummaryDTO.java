package com.ladysparks.ttaenggrang.domain.weekly_report.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.ladysparks.ttaenggrang.domain.weekly_report.entity.WeeklyFinancialSummary;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(value = {"studentId", "reportDate"})
@Builder
public class WeeklyFinancialSummaryDTO {

    private Long studentId;
    private LocalDate reportDate;
    private int totalIncome;       // 총 수입
    private int salaryAmount;      // 급여액
    private int savingsAmount;     // 저축액
    private int investmentReturn;  // 투자 수익
    private int incentiveAmount;   // 인센티브
    private int totalExpenses;     // 소비액
    private int taxAmount;         // 세금 납부액
    private int fineAmount;        // 벌금 납부액

    public WeeklyFinancialSummaryDTO(WeeklyFinancialSummary summary) {
        this.studentId = summary.getStudent().getId();
        this.reportDate = summary.getReportDate();
        this.totalIncome = summary.getTotalIncome();
        this.salaryAmount = summary.getSalaryAmount();
        this.savingsAmount = summary.getSavingsAmount();
        this.investmentReturn = summary.getInvestmentReturn();
        this.incentiveAmount = summary.getIncentiveAmount();
        this.totalExpenses = summary.getTotalExpenses();
        this.taxAmount = summary.getTaxAmount();
        this.fineAmount = summary.getFineAmount();
    }

}
