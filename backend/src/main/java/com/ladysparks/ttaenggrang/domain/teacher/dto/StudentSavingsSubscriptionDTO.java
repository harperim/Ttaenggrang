package com.ladysparks.ttaenggrang.domain.teacher.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StudentSavingsSubscriptionDTO {

    private LocalDate subscriptionDate; // 날짜 (가입)
    private String savingsName;         // 상품명
    private String amount;              // 금액 (월 납입 금액)
    private float interest;             // 이자율
    private int totalAmount;            // 잔액(현재 총 납입금)

}
