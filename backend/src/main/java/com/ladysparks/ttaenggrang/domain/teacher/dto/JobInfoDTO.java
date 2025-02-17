package com.ladysparks.ttaenggrang.domain.teacher.dto;

import lombok.Builder;
import lombok.Data;

@Data
public class JobInfoDTO {
    private String jobName;
    private int baseSalary;

    @Builder
    public JobInfoDTO(String jobName, int baseSalary) {
        this.jobName = jobName;
        this.baseSalary = baseSalary;
    }
}


