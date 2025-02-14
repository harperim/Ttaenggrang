package com.ladysparks.ttaenggrang.domain.stock.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ladysparks.ttaenggrang.domain.stock.entity.Stock;
import lombok.*;


import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockDTO {
    private Long id;            // 주식 ID
    private String name;        // 주식 이름
    private int price_per;      // 한 주당 가격
    private int total_qty;      // 총 수량
    private int remain_qty;     // 주식 재고 수량
    private String description; // 설명
    private Timestamp created_at; // 생성일
    private Timestamp updated_at;  // 수정일
    private Integer changeRate;   // 주식 변동률
    private final String type = "일반 주식";
    private LocalDateTime priceChangeTime;  // 가격 변동 시간
    private BigDecimal weight;  // 주식 비중 (각 주식의 비중을 따로 저장)




    //조인
    private Long teacher_id;    // 교사 ID
    @JsonIgnore
    private Long etf_id;         // ETF ID

    private Long categoryId;

    private String categoryName;

    private Long market_status_id;





    public static Stock toEntity(StockDTO stockDto) {
        return Stock.builder()
                .id(stockDto.getId())
                .name(stockDto.getName())
                .price_per(stockDto.getPrice_per())
                .total_qty(stockDto.getTotal_qty())
                .remain_qty(stockDto.getRemain_qty())
                .description(stockDto.getDescription())
                .created_at(stockDto.getCreated_at())
                .updated_at(stockDto.getUpdated_at())
                .changeRate(stockDto.getChangeRate())
//                .isMarketActive(stockDto.getIsMarketActive())  // 주식장 활성화 여부
                .priceChangeTime(stockDto.getPriceChangeTime())  // 가격 변동 시
                .build();
    }

    public static StockDTO fromEntity(Stock stock) {
        // builder로 DTO 객체 생성
        StockDTO.StockDTOBuilder dtoBuilder = StockDTO.builder()
                .id(stock.getId())
                .name(stock.getName())
                .price_per(stock.getPrice_per())
                .total_qty(stock.getTotal_qty())
                .remain_qty(stock.getRemain_qty())
                .description(stock.getDescription())
                .created_at(stock.getCreated_at())
                .updated_at(stock.getUpdated_at())
                .changeRate(stock.getChangeRate())
//                .isMarketActive(stock.getIsMarketActive())  // 주식장 활성화 여부
                .priceChangeTime(stock.getPriceChangeTime());  // 가격 변동 시

        // 카테고리 정보가 있으면 추가
        if (stock.getCategory() != null) {
            dtoBuilder.categoryId(stock.getCategory().getId());
            dtoBuilder.categoryName(stock.getCategory().getName());  // 카테고리 이름 추가
        }

        // DTO 반환
        return dtoBuilder.build();
    }
}



