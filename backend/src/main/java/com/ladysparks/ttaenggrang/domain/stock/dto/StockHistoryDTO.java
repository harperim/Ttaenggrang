package com.ladysparks.ttaenggrang.domain.stock.dto;

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

    //조인
    // Stock
    private int stockId;    // stock_id 외래 키

    // Etf 관련 (etf_id 외래 키를 참조)
    private int etfId;      // etf_id 외래 키
}
