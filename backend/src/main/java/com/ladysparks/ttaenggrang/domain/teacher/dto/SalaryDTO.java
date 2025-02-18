package com.ladysparks.ttaenggrang.domain.teacher.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(value={"id", "studentName", "baseSalary", "totalSalary"}, allowGetters=true)
public class SalaryDTO {

    private long id;
    private String studentName;
    private Integer baseSalary;  // 기본 급여
    private Integer totalTax;  // 총 세금 금액
    private Integer netSalary;  // 실수령액
    private Map<String, Integer> taxDetails;  // 세금 항목별 공제 금액 (ex: 소득세 - 1000원)
}
