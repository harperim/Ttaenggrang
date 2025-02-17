package com.ladysparks.ttaenggrang.domain.teacher.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobClassDTO {  // 우리 반 직업 정보
    private Long id;
    private String jobName;
    private String jobDescription;
    private Integer baseSalary;
    private Integer maxPeople;
}
