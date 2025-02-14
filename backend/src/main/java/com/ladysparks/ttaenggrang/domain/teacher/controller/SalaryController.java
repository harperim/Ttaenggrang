package com.ladysparks.ttaenggrang.domain.teacher.controller;

import com.ladysparks.ttaenggrang.domain.teacher.dto.IncentiveDTO;
import com.ladysparks.ttaenggrang.domain.teacher.repository.TeacherRepository;
import com.ladysparks.ttaenggrang.domain.teacher.service.IncentiveService;
import com.ladysparks.ttaenggrang.domain.teacher.service.SalaryService;
import com.ladysparks.ttaenggrang.global.docs.nation.SalaryApiSpecification;
import com.ladysparks.ttaenggrang.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/salaries")
@RequiredArgsConstructor
public class SalaryController implements SalaryApiSpecification {

    private final TeacherRepository teacherRepository;
    private final SalaryService salaryService;
    private final IncentiveService incentiveService;

    // (+) 현재 로그인한 교사의 ID 가져오는 메서드
    private long getTeacherIdFromSecurityContext() {
        // 🔥 인증 객체 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        System.out.println("★인증 객체: " + authentication);

        // ✅ 인증 정보가 없을 경우 예외 발생 방지
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalArgumentException("인증되지 않은 사용자입니다. 로그인 후 다시 시도하세요.");
        }

        // Principal 가져오기 : SecurityContextHolder에서 인증된 사용자 정보 가져오기
        Object principalObj = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principalObj instanceof UserDetails) {
            String email = ((UserDetails) principalObj).getUsername();
            return teacherRepository.findByEmail(email)
                    .orElseThrow(() -> new IllegalArgumentException("해당 이메일을 가진 교사를 찾을 수 없습니다."))
                    .getId();
        }
        throw new IllegalArgumentException("현재 인증된 사용자를 찾을 수 없습니다.");
    }

    @PostMapping("/distribute-base")
    public ResponseEntity<ApiResponse<String>> distributeBaseSalary() {
        ApiResponse<String> response = salaryService.distributeBaseSalary();
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PostMapping("/incentives")
    public ResponseEntity<ApiResponse<String>> giveIncentive(@RequestBody IncentiveDTO incentiveDTO) {
        ApiResponse<String> response = incentiveService.giveIncentive(incentiveDTO);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}
