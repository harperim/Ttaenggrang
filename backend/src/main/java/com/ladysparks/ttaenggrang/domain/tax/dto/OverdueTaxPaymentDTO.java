package com.ladysparks.ttaenggrang.domain.tax.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OverdueTaxPaymentDTO {

    private String taxName;      // 세금명
    private String description;  // 미납 사유
    private int totalAmount;     // 미납된 세금 총합


    public interface OverdueTaxPaymentProjection {
        String getDescription();   // '벌금'
        String getTaxNames();      // '소득세, 주민세, 부가세'
        int getTotalAmount();      // 총 미납액
    }

}
