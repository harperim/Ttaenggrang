package com.ladysparks.ttaenggrang.domain.bank.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.ladysparks.ttaenggrang.domain.bank.entity.SavingsGoal.SavingsGoalStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@JsonIgnoreProperties(value = {"id", "status", "createdAt"}, allowGetters = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class SavingsGoalDTO {

    private Long id;
    private Long studentId;
    private String name;
    private int targetAmount;
    private SavingsGoalStatus status;
    private Timestamp createdAt;

}
