package com.ladysparks.ttaenggrang.domain.tax.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.validator.constraints.Range;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonIgnoreProperties(value = {"id", "teacherId"}, allowGetters = true)
public class TaxDTO {

    private Long id;

    private Long teacherId;

    @NotNull(message = "세금명(taxName)은 필수 항목입니다.")
    private String taxName;

    @NotNull(message = "세율(taxRate)은 필수 항목입니다.")
    @Range(min = 0, max = 100, message = "0 < taxRate(세율) < 100 값만 입력 가능합니다.")
    private BigDecimal taxRate;

    @NotNull(message = "세금 설명(taxDescription)은 필수 항목입니다.")
    private String taxDescription;

}
