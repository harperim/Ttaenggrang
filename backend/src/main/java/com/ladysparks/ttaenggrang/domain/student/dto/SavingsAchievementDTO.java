package com.ladysparks.ttaenggrang.domain.student.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SavingsAchievementDTO {

    private Long studentId;
    private Double savingsAchievementRate;
    private int rank;

}