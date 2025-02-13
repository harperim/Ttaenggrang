package com.ladysparks.ttaenggrang.domain.stock.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MarketStatusDTO {
    private Long id;

    private boolean isMarketActive;  // 현재 시장 활성화 상태
    private boolean isManualOverride;  // 선생님 버튼으로 수동 설정 여부

    //조인
    private Long stock_id;
    private Long etf_id;         // ETF ID

}
