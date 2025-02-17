package com.ladysparks.ttaenggrang.domain.weekly_report.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FinancialGrowthDTO {

    private double savingsGrowthRate;     // 저축 증가율
    private double investmentReturnRate;  // 투자 수익률
    private double expenseGrowthRate;     // 지출 증가율

}

