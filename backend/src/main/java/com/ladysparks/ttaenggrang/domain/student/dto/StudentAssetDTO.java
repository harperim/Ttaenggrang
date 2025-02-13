package com.ladysparks.ttaenggrang.domain.student.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentAssetDTO {

    @Schema(description = "학생 ID", example = "1")
    private Long studentId;

    @Schema(description = "총 자산 (계좌 잔액 + 저축 + 투자 수익)", example = "250000")
    private int totalAsset;

    @Schema(description = "급여", example = "50000")
    private int totalSalary;

    @Schema(description = "총 저축 (적금 납입 중인 금액 + 적금 만기 지급 금액)", example = "70000")
    private int totalSavings;

    @Schema(description = "투자 수익 (매도 금액 + 투자 평가액)", example = "30000")
    private int totalInvestmentProfit;

    @Schema(description = "인센티브", example = "15000")
    private int totalIncentive;

    @Schema(description = "총 소비", example = "60000")
    private int totalExpense;

}
