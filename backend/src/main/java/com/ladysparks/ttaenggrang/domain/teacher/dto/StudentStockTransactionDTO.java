package com.ladysparks.ttaenggrang.domain.teacher.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StudentStockTransactionDTO {

    private Long stockId;           // 주식 ID
    private String stockName;       // 주식명
    private int quantity;           // 보유 수량
    private int currentTotalPrice;  // 현재가 (총 평가 금액)
    private int purchasePrice;      // 주당 구매 가격
    private int priceChangeRate;    // 주가 변동률

}
