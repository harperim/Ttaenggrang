package com.ladysparks.ttaenggrang.domain.student.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentDashboardDTO {

    private Long studentId;      // 학생 ID
    private int accountBalance;  // 계좌 잔액
    private int currentRank;     // 현재 순위
    private int totalSavings;   // 적금 납입액
    private int totalInvestmentAmount; // 투자 평가액
    private int totalAsset;     // 전체 자산 (적금 + 계좌 잔액 + 투자 평가액)
    private int goalAmount;      // 목표액
    private double achievementRate; // 목표 달성률 (%)

}
