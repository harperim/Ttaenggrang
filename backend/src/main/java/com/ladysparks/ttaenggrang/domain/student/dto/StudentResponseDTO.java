package com.ladysparks.ttaenggrang.domain.student.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.ladysparks.ttaenggrang.domain.bank.entity.BankAccount;
import com.ladysparks.ttaenggrang.domain.teacher.dto.JobInfoDTO;
import com.ladysparks.ttaenggrang.domain.teacher.entity.Job;
import com.ladysparks.ttaenggrang.domain.teacher.entity.Teacher;
import lombok.Data;

@Data
//@AllArgsConstructor
@JsonIgnoreProperties(value = {"id", "token", "profileImageUrl"}, allowGetters = true)
public class StudentResponseDTO {
    private Long id;
    private String username;  // 생성된 학생 계정
    private String name;
    private String profileImageUrl;

    private Teacher teacher;
    private BankAccount bankAccount;
    private JobInfoDTO jobInfo;
    private String token;

    // ✅ 기본 조회용 생성자 (id 없이 username과 password만 받음)
    public StudentResponseDTO(String username) {
        this.username = username;
    }

    // ✅ 학생 정보 조회용 생성자
    public StudentResponseDTO(Long id, String username, String name, String profileImageUrl, Teacher teacher, BankAccount bankAccount, JobInfoDTO jobInfo, String token) {
        this.id = id;
        this.username = username;
        this.name = name;
        this.profileImageUrl = profileImageUrl;
        this.teacher = teacher;
        this.bankAccount = bankAccount;
        this.jobInfo = jobInfo;
        this.token = token;
    }
}
