package com.ladysparks.ttaenggrang.domain.weekly_report.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentFinancialSummaryDTO {

    private Long studentId;
    private FinancialGrowthDTO lastWeekSummary;
    private FinancialGrowthDTO thisWeekSummary;
    private FinancialGrowthDTO classAverageSummary;

}
