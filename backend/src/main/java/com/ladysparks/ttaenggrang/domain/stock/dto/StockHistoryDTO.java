package com.ladysparks.ttaenggrang.domain.stock.dto;

import com.ladysparks.ttaenggrang.domain.stock.entity.StockHistory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockHistoryDTO {

    private Long id;
    private int price;
    private int buyVolume;
    private int sellVolume;
    private Timestamp date;

    // 조인
    // Stock
    private Long stockId;     // stock_id 외래 키

    // Etf 관련 (etf_id 외래 키를 참조)
    private Long etfId;      // etf_id 외래 키

    public static StockHistoryDTO fromEntity(StockHistory stockHistory) {
        return StockHistoryDTO.builder()
                .id(stockHistory.getId())
                .price(stockHistory.getPrice())  // 가격
                .buyVolume(stockHistory.getBuyVolume())  // 구매량
                .sellVolume(stockHistory.getSellVolume())  // 판매량
                .date(stockHistory.getCreatedAt())  // 거래 일자
                .stockId(stockHistory.getStock() != null ? Long.valueOf(stockHistory.getStock().getId()) : null)  // 주식 ID
                .etfId(stockHistory.getEtf() != null ? Long.valueOf(stockHistory.getEtf().getId()) : null)  // ETF ID
                .build();
    }

}
