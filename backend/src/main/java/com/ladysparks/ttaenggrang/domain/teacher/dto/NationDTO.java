package com.ladysparks.ttaenggrang.domain.teacher.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(value={"id", "teacherId", "establishedDate", "profileImageUrl", "teacher"}, allowGetters=true)
public class NationDTO {

    private Long id;

    private Long teacherId;

    @NotBlank(message = "국가 이름은 필수 항목입니다.")
    private String nationName;

    @Min(value = 1, message = "인구는 양의 정수여야 합니다.")
    private Integer population;

    @NotBlank(message = "통화 단위는 필수 항목입니다.")
    private String currency;

    private Integer savingsGoalAmount;

    private Integer nationalTreasury;

    private Timestamp establishedDate;

    private String profileImageUrl;

}
