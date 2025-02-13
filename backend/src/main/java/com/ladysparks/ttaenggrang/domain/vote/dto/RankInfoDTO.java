package com.ladysparks.ttaenggrang.domain.vote.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class RankInfoDTO {

    private int rank;  // 등수
    private int votes;  // 득표 수
    private String studentName;  // 학생 이름

    public RankInfoDTO(int rank, int votes, String studentName) {
        this.rank = rank;
        this.votes = votes;
        this.studentName = studentName;
    }
}
