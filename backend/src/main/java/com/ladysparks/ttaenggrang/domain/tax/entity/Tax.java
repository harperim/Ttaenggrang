package com.ladysparks.ttaenggrang.domain.tax.entity;

import com.ladysparks.ttaenggrang.domain.teacher.entity.Teacher;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tax {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private Teacher teacher;

    @Column(nullable = false)
    private String taxName;

    @DecimalMin(value = "0.00", inclusive = false, message = "세율은 0보다 커야 합니다.")
    @DecimalMax(value = "1.00", inclusive = false, message = "세율은 1보다 작아야 합니다.")
    @Column(precision = 10, scale = 2, nullable = false) // 🔥 BigDecimal을 사용할 경우 precision 지정
    private BigDecimal taxRate;

    @Column(nullable = false, length = 500)
    private String taxDescription;

}
