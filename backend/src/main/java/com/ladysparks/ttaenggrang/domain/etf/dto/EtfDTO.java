package com.ladysparks.ttaenggrang.domain.etf.dto;

import com.google.gson.Gson;
import com.ladysparks.ttaenggrang.domain.etf.entity.Etf;
import jakarta.persistence.Column;
import lombok.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EtfDTO {
    private Long id;
    private String name;
    private int price_per;
    private int total_qty;
    private int remain_qty;
    private String description;
    private Timestamp created_at;
    private Timestamp updated_at;
    private Integer changeRate; //가격 변동률
    private final String type = "ETF";
    private String stockDataJson; // 주식 ID + 수량을 JSON 형태로 저장
    private LocalDateTime priceChangeTime;  // 가격 변동 시간
    private String trackedIndex; // 추적하는 지수




    //조인
    private List<Long> stock_id;  // 주식
    private Long teacher_id; //선생님
    private Long market_status_id;

    // ✅ ETF를 구성하는 개별 주식 정보 리스트 추가
    private List<EtfCompositionDTO> compositions;





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
                .stockDataJson(etfDto.getStockDataJson())
                .priceChangeTime(etfDto.getPriceChangeTime())
                .trackedIndex(etfDto.getTrackedIndex())

                .build();
    }

    public static EtfDTO fromEntity(Etf etf) {
        // JSON을 Map으로 변환

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
                .stockDataJson(etf.getStockDataJson()) // DTO에서 JSON 대신 Map으로 변환한 데이터를 사용
                .priceChangeTime(etf.getPriceChangeTime())
                .trackedIndex(etf.getTrackedIndex())
                .build();

    }

}
