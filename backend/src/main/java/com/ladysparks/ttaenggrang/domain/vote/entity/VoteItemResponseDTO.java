package com.ladysparks.ttaenggrang.domain.vote.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VoteItemResponseDTO {
    private Long voteItemId;    // 투표 항목 ID
    private Long studentId;     // 학생 ID
    private String studentName; // 학생 이름
    private String profileImageUrl;  // 프로필 이미지 URL (선택)
    private int voteCount;      // 현재 득표 수
}
