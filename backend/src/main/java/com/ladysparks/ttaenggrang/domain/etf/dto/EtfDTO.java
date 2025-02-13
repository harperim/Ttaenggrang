package com.ladysparks.ttaenggrang.domain.etf.dto;

import com.ladysparks.ttaenggrang.domain.etf.entity.Etf;
import jakarta.persistence.Column;
import lombok.*;

import java.sql.Timestamp;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EtfDTO {
    private Long id;
    private Long teacher_id;
    private String name;
    private int price_per;
    private int total_qty;
    private int remain_qty;
    private String description;
    private Timestamp created_at;
    private Timestamp updated_at;
    private Integer changeRate; //가격 변동률
//    private Boolean isMarketActive;  //주식장 활성화

    private List<Long> stock_id;  // 주식

    private Long market_status_id;



    // lombok을 사용한 Entity 객체 생성(builder) 방법
    public static Etf toEntity(EtfDTO etfDto) {
        return Etf.builder()
                .id(etfDto.getId())
                .name(etfDto.getName())
                .price_per(etfDto.getPrice_per())
                .total_qty(etfDto.getTotal_qty())
                .remain_qty(etfDto.getRemain_qty())
                .description(etfDto.getDescription())
                .created_at(etfDto.getCreated_at())
                .updated_at(etfDto.getUpdated_at())
                .changeRate(etfDto.getChangeRate())
//                .isMarketActive(etfDto.getIsMarketActive())
                .build();
    }

    public static EtfDTO fromEntity(Etf etf) {
        return EtfDTO.builder()
                .id(etf.getId())
                .name(etf.getName())
                .price_per(etf.getPrice_per())
                .total_qty(etf.getTotal_qty())
                .remain_qty(etf.getRemain_qty())
                .description(etf.getDescription())
                .created_at(etf.getCreated_at())
                .updated_at(etf.getUpdated_at())
                .changeRate(etf.getChangeRate())
//                .isMarketActive(etf.getIsMarketActive())
                .build();
    }



}
