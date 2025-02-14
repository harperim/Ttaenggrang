package com.ladysparks.ttaenggrang.domain.teacher.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
@JsonIgnoreProperties(value={"id"}, allowGetters=true)
public class MultipleStudentCreateDTO {

    private Long id;

    @NotEmpty(message = "베이스 ID를 입력하세요.")
    private String baseId;  // 베이스ID (ex: "student")

    @Min(value = 1, message = "학생 수는 최소 1명 이상이어야 합니다.")
    private int studentCount;  // 생성할 학생 계정 수

    @Schema(type = "string", format = "binary", description = "학생 이름이 포함된 파일 업로드 (CSV 또는 XLSX 형식)", required = false)
    private MultipartFile file;
}
