package com.ladysparks.ttaenggrang.domain.student.dto;


import com.ladysparks.ttaenggrang.domain.bank.entity.BankAccount;
import com.ladysparks.ttaenggrang.domain.teacher.entity.Teacher;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Base64;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentLoginResponseDTO {

    private Long id;
    private String username;
    private String name;
    private String profileImage;
    private Teacher teacher;
    private BankAccount bankAccount;
    private String token;

    // ✅ `setProfileImage`에서 빈 문자열("")을 허용하도록 수정
    public void setProfileImage(byte[] imageBytes) {
        if (imageBytes != null && imageBytes.length > 0) {
            this.profileImage = Base64.getEncoder().encodeToString(imageBytes);
        } else {
            this.profileImage = null;  // 빈 문자열이면 `null`로 변환
        }
    }
}
