package com.ladysparks.ttaenggrang;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

// 비밀번호를 토큰 값으로 바꿔주는 파일
public class PasswordEncryptor {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        // 암호화할 비밀번호 입력
        String rawPassword = "ssafy123";
        String encodedPassword = encoder.encode(rawPassword);

        // 암호화된 비밀번호 출력 (이걸 DB에 저장하세요)
        System.out.println("암호화된 비밀번호: " + encodedPassword);
    }
}
