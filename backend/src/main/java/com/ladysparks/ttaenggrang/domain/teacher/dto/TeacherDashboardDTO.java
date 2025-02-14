package com.ladysparks.ttaenggrang.domain.teacher.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeacherDashboardDTO {

    // 국고 수입
    private int nationalTreasuryIncome;

    // 1인 평균 잔고
    private int averageStudentBalance;

    // 판매 중인 상품 개수
    private int activeItemCount;

    // 우리 반 목표 저축액
    private int classSavingsGoal;

}
