package com.ladysparks.ttaenggrang.domain.etf.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EtfCompositionDTO {
    private Long etfId;        // ETF ID
    private Long stockId;      // 개별 주식 ID
    private String stockName;  // 개별 주식 이름
    private double weight;     // ETF에서 해당 주식의 비율 (0~1 사이 값)
    private int stockPrice;    // 현재 주식 가격
    private int stockChangeRate; // 주식 변동률

}
