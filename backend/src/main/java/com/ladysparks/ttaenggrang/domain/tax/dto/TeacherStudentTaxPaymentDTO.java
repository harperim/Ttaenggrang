package com.ladysparks.ttaenggrang.domain.tax.dto;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TeacherStudentTaxPaymentDTO {

    private Long studentId;
    private String studentName;
    private Long totalAmount;

}