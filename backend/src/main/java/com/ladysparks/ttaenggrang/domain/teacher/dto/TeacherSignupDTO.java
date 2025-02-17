package com.ladysparks.ttaenggrang.domain.teacher.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@JsonIgnoreProperties(value={"id", "createdAt"}, allowGetters=true)
public class TeacherSignupDTO {

    private Long id;

    @NotEmpty(message="이메일은 필수항목입니다.")
    @Email(message="올바른 이메일 형식이 아닙니다.")
    private String email;

    @NotEmpty(message="비밀번호는 필수항목입니다.")
    @Size(message="비밀번호는 최소 6자 이상이어야 합니다.")
    private String password1;

    @NotEmpty(message="비밀번호 확인을 입력하세요.")
    private String password2;

    @Size(min = 2, max = 25)
    @NotEmpty(message="이름을 입력하세요.")
    private String name;

    @NotEmpty(message="초등학교는 필수항목입니다.")
    private String school;
    private Timestamp createdAt;
}
