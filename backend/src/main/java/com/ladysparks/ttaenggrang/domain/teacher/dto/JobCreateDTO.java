package com.ladysparks.ttaenggrang.domain.teacher.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(value={"id", "studentIds"}, allowGetters=true)
public class JobCreateDTO {

    private Long id;

    @NotBlank(message = "직업 이름을 입력하세요.")
    private String jobName;

    @NotBlank(message = "직업 설명을 입력하세요.")
    private String jobDescription;

    @NotNull(message = "기본 월급을 입력하세요.")
    @Min(value = 1, message = "기본 월급은 1 이상이어야 합니다.")
    private Integer baseSalary;

    @NotNull(message = "최대 인원을 입력하세요.")
    @Min(value = 1, message = "최대 인원은 최소 1명 이상이어야 합니다.")
    private Integer maxPeople;

    private List<Long> studentIds;
}
