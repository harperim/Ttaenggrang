package com.ladysparks.ttaenggrang.domain.stock.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChangeResponseDTO {

    private Long id;                    // 주식 ID
    private String name;                // 주식 이름
    private int pricePerShare;          // 한 주당 가격
    private int totalQuantity;          // 총 수량
    private String description;         // 설명
    private Integer changeRate;         // 주식 변동률
    private int remainQuantity;         // 주식 재고 수량
    private Boolean isMarketOpen;       //시장 활성화 여부

}
