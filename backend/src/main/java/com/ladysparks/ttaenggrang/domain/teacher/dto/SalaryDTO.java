package com.ladysparks.ttaenggrang.domain.teacher.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(value={"id", "studentName", "baseSalary", "totalSalary"}, allowGetters=true)
public class SalaryDTO {

    private long id;
    private String studentName;
    private Integer baseSalary;
    private Integer totalSalary;
}
