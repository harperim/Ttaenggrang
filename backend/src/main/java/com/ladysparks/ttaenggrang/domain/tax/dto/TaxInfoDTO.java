package com.ladysparks.ttaenggrang.domain.tax.dto;

import com.ladysparks.ttaenggrang.domain.tax.entity.TaxInfo;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaxInfoDTO {
    private Long id;
    private String name;  //세금명
    private int taxAmt; //사용 금액
    private String description; //설명
    private Timestamp useDate; //사용 날짜

    public static TaxInfoDTO fromEntity(TaxInfo taxInfo) {
        return TaxInfoDTO.builder()
                .name(taxInfo.getName())
                .taxAmt(taxInfo.getTaxAmt())
                .description(taxInfo.getDescription())
                .useDate(taxInfo.getUseDate())
                .build();
    }

}
