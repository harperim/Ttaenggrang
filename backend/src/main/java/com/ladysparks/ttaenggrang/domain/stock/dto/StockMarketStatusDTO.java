package com.ladysparks.ttaenggrang.domain.stock.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockMarketStatusDTO {
    
    private Long teacherId;

    @JsonProperty("isMarketOpen")
    private boolean isMarketOpen;      // 현재 시장 활성화 상태

    @JsonProperty("isTeacherOn")
    private boolean isTeacherOn;       // 교사 버튼으로 수동 설정 여부

}
