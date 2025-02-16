package com.ladysparks.ttaenggrang.domain.tax.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.ladysparks.ttaenggrang.domain.tax.entity.TaxUsage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(value = {"usageDate"}, allowGetters = true)
public class TaxUsageDTO {

    private String name;            // 사용 내역 (세금명)
    private int amount;             // 사용 금액
    private String description;     // 설명
    private LocalDate usageDate;    // 사용 날짜

    public static TaxUsageDTO toDto(TaxUsage taxUsage) {
        return TaxUsageDTO.builder()
                .name(taxUsage.getName())
                .amount(taxUsage.getAmount())
                .description(taxUsage.getDescription())
                .usageDate(taxUsage.getUsageDate())
                .build();
    }

    public static TaxUsage toEntity(TaxUsageDTO taxUsageDTO) {
        return TaxUsage.builder()
                .name(taxUsageDTO.getName())
                .amount(taxUsageDTO.getAmount())
                .description(taxUsageDTO.getDescription())
                .usageDate(taxUsageDTO.getUsageDate())
                .build();
    }

}
