package com.ladysparks.ttaenggrang.domain.teacher.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IncentiveDTO {
    private long studentId;
    private int incentive;
}
