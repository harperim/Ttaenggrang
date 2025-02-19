package com.ladysparks.ttaenggrang.domain.stock.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ladysparks.ttaenggrang.domain.stock.entity.Stock;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockDTO {

    private Long id;                        // 주식 ID
    private String name;                    // 주식 이름
    private int pricePerShare;              // 한 주당 가격
    private int totalQuantity;              // 총 수량
    private int remainQuantity;             // 주식 재고 수량
    private String description;             // 설명
    private Timestamp createdAt;            // 생성일
    private Timestamp updatedAt;            // 수정일
    private Integer changeRate;             // 주식 변동률
    private final String type = "일반 주식";
    private LocalDateTime priceChangeTime;  // 가격 변동 시간
    private BigDecimal weight;              // 주식 비중 (각 주식의 비중을 따로 저장)
    private Long teacherId;                 // 교사 ID
    @JsonIgnore
    private Long etfId;                     // ETF ID
    private Long categoryId;
    private String categoryName;

//    private Long marketStatusId;

    public static Stock toEntity(StockDTO stockDto) {
        return Stock.builder()
                .id(stockDto.getId())
                .name(stockDto.getName())
                .price_per(stockDto.getPricePerShare())
                .total_qty(stockDto.getTotalQuantity())
                .remain_qty(stockDto.getRemainQuantity())
                .description(stockDto.getDescription())
                .created_at(stockDto.getCreatedAt())
                .updated_at(stockDto.getUpdatedAt())
                .changeRate(stockDto.getChangeRate())
//                .isMarketActive(stockDto.getIsMarketActive())  // 주식장 활성화 여부
                .priceChangeTime(stockDto.getPriceChangeTime())  // 가격 변동 시
                .build();
    }

    public static StockDTO fromEntity(Stock stock) {
        // builder로 DTO 객체 생성
        StockDTO.StockDTOBuilder dtoBuilder = StockDTO.builder()
                .id(stock.getId())
                .teacherId(stock.getTeacher().getId())
                .name(stock.getName())
                .pricePerShare(stock.getPrice_per())
                .totalQuantity(stock.getTotal_qty())
                .remainQuantity(stock.getRemain_qty())
                .description(stock.getDescription())
                .createdAt(stock.getCreated_at())
                .updatedAt(stock.getUpdated_at())
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