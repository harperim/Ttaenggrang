package com.ladysparks.ttaenggrang.global.utill;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class SecurityUtil {

    public String getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalArgumentException("인증되지 않은 사용자입니다. 로그인 후 다시 시도하세요.");
        }

        Object principalObj = authentication.getPrincipal();
        if (principalObj instanceof UserDetails userDetails) {
            return userDetails.getUsername(); // 이메일만 반환
        }

        throw new IllegalArgumentException("현재 인증된 사용자를 찾을 수 없습니다.");
    }

    // 블랙리스트로 사용할 Set (메모리 기반)
    public final Set<String> blacklist = new HashSet<>();

    // 토큰을 블랙리스트에 추가하는 메서드
    public void blacklistToken(String token) {
        blacklist.add(token);
    }

    // 블랙리스트에 토큰이 존재하는지 확인하는 메서드
    public boolean isTokenBlacklisted(String token) {
        return blacklist.contains(token);
    }

}
