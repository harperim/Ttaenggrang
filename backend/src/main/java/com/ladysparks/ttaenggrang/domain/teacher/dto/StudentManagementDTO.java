package com.ladysparks.ttaenggrang.domain.teacher.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StudentManagementDTO {

    private Long studentId;
    private String studentName;
    private String username;
    private String jobName;
    private int baseSalary;
    private int accountBalance;

}