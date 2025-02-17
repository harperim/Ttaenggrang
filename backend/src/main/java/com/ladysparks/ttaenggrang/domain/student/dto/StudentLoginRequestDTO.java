package com.ladysparks.ttaenggrang.domain.student.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Optional;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(value={"id"}, allowGetters=true)
public class StudentLoginRequestDTO {

    @NotEmpty(message = "아이디를 입력하세요.")
    private String username;

    @NotEmpty(message = "비밀번호는 필수항목입니다.")
    private String password;

    private Optional<String> fcmToken;

}
