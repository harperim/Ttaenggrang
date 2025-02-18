package com.ladysparks.ttaenggrang.domain.tax.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TaxPaymentDTO {

    private Long id;
    private Long studentId;
    private Long taxId;
    private LocalDate paymentDate;
    private String taxName;
    private String taxDescription;
    private BigDecimal taxRate;
    private int amount;
    private boolean isOverdue;

}