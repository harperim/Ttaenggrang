package com.ladysparks.ttaenggrang.domain.teacher.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(value={"id", "name", "school", "hasNation", "token"}, allowGetters=true)
public class TeacherLoginDTO {
    private Long id;

    @NotEmpty(message = "이메일을 입력하세요.")
    @Email(message = "유효하지 않은 이메일 형식입니다.")
    private String email;

    @NotEmpty(message="비밀번호는 필수항목입니다.")
    private String password;

    private String name;
    private String school;
    private boolean hasNation;
    private String token;

    // ✅ 요청을 위한 생성자
    public TeacherLoginDTO(String email, String password) {
        this.email = email;
        this.password = password;
    }

    // ✅ 응답을 위한 생성자
    public TeacherLoginDTO(String email, String name, String school, boolean hasNation, String token) {
        this.email = email;
        this.name = name;
        this.school = school;
        this.hasNation = hasNation;
        this.token = token;
    }
}
