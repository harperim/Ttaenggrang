package com.ladysparks.ttaenggrang.domain.stock.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentStockDTO {

    private Long stockId;               // 주식 ID
    private String stockName;           // 주식명
    private int quantity;               // 보유 주식 수
    private int purchasePrice;          // 최초 구매 가격
    private LocalDateTime purchaseDate; // 최초 구매 날짜
    private int currentPrice;           // 현재 주가

}
