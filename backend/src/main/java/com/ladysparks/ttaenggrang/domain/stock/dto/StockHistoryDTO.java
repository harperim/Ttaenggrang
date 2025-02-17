package com.ladysparks.ttaenggrang.domain.stock.dto;

import com.ladysparks.ttaenggrang.domain.stock.entity.StockHistory;
import lombok.*;

import java.sql.Timestamp;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StockHistoryDTO {

    private Long id;
    private Long stockId;           // stock_id 외래 키
    private int price;              // 해당 날짜의 주식 가격
//    private int buyVolume;        // 매수량
//    private int sellVolume;       // 매도량
    private int priceChangeRate;    // 가격 변동률
    private Timestamp date;         // 기록 날짜
//    private Long etfId;           // etf_id 외래 키

    public static StockHistoryDTO fromEntity(StockHistory stockHistory) {
        return StockHistoryDTO.builder()
                .id(stockHistory.getId())
                .stockId(stockHistory.getStock() != null ? stockHistory.getStock().getId() : null)
                .price(stockHistory.getPrice())
//                .buyVolume(stockHistory.getBuyVolume())
//                .sellVolume(stockHistory.getSellVolume())
                .priceChangeRate(stockHistory.getPriceChangeRate())  // 가격 변동률 추가
                .date(stockHistory.getCreatedAt())
//                .etfId(stockHistory.getEtf() != null ? stockHistory.getEtf().getId() : null)
                .build();
    }

}

