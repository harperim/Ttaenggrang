package com.ladysparks.ttaenggrang.domain.bank.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.sql.Timestamp;
import java.time.LocalDate;

@JsonIgnoreProperties(value = {"id", "teacherId", "subscriberCount", "createdAt"}, allowGetters = true)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SavingsProductDTO {

    private Long id;
    private Long teacherId;

    @NotEmpty(message = "상품명(name)은 필수 항목입니다.")
    private String name;

    private String description;

    @NotNull(message = "이자율(interestRate)은 필수 항목입니다.")
    private Float interestRate;

    @NotNull(message = "중도 해지 이자율(earlyInterestRate)은 필수 항목입니다.")
    private Float earlyInterestRate;

    @NotNull(message = "가입 기간(durationWeeks)은 필수 항목입니다.")
    private Integer durationWeeks;

    @NotNull(message = "납입 금액(amount)은 필수 항목입니다.")
    private Integer amount;

    @NotNull(message = "판매 시작일(saleStartDate)은 필수 항목입니다.")
    private LocalDate saleStartDate;

    @NotNull(message = "판매 종료일(saleEndDate)은 필수 항목입니다.")
    private LocalDate saleEndDate;

    private int subscriberCount;

    private Timestamp createdAt;

}
