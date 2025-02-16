package com.ladysparks.ttaenggrang.domain.teacher.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class StudentJobUpdateResponseDTO {
    private Long studentId;
    private String name;
    private String username;
    private String password;
    private JobInfoDTO jobInfo;
}
