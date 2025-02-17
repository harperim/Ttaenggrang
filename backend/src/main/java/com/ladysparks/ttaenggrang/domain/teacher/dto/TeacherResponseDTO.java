package com.ladysparks.ttaenggrang.domain.teacher.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(value={"id", "createdAt"}, allowGetters=true)
public class TeacherResponseDTO {
    private Long id;
    private String email;
    private String name;
    private String school;
    private Timestamp createdAt;
}
