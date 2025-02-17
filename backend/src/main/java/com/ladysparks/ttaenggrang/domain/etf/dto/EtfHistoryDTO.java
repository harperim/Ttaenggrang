package com.ladysparks.ttaenggrang.domain.etf.dto;

import com.ladysparks.ttaenggrang.domain.etf.entity.EtfHistory;
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
public class EtfHistoryDTO {
    private Long id;
    private int price;
    private int buyVolume;
    private int sellVolume;
    private Timestamp date;

    // 조인
    // Etf 관련 (etf_id 외래 키를 참조)
    private Long etfId;      // etf_id 외래 키


    public static com.ladysparks.ttaenggrang.domain.etf.dto.EtfHistoryDTO fromEntity(EtfHistory etfHistory) {
        return com.ladysparks.ttaenggrang.domain.etf.dto.EtfHistoryDTO.builder()
                .id(etfHistory.getId())
                .price(etfHistory.getPrice())  // 가격
                .buyVolume(etfHistory.getBuyVolume())  // 구매량
                .sellVolume(etfHistory.getSellVolume())  // 판매량
                .date(etfHistory.getCreatedAt())  // 거래 일자
//                .stockId(etfHistory.getStock() != null ? Long.valueOf(etfHistory.getStock().getId()) : null)  // 주식 ID
                .etfId(etfHistory.getEtf() != null ? Long.valueOf(etfHistory.getEtf().getId()) : null)  // ETF ID
                .build();
        }
}
