package com.ladysparks.ttaenggrang.domain.etf.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EtfSummaryDTO {
    private Long id;                    // 주식 ID
    private LocalDate createdDate;      // 등록일
    private String name;                // 종목명
    private String type;                // 주식 종류 (일반 주식/ETF)
    private String category;            // 카테고리
    private int price_per;          // 현재 가격 (한 주당 가격)
    private Integer priceChangeRate;    // 주식 가격 변동률 (%) = (오늘 가격 - 어제 가격) / 어제 가격 * 100
    private int transactionFrequency;   // 거래 활성도 (%) = 최근 7일 거래량 / 전체 주식의 평균 거래량 * 100
}
