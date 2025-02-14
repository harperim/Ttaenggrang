package com.ladysparks.ttaenggrang.domain.teacher.controller;

import com.ladysparks.ttaenggrang.domain.teacher.dto.TeacherLoginDTO;
import com.ladysparks.ttaenggrang.domain.teacher.dto.TeacherResponseDTO;
import com.ladysparks.ttaenggrang.domain.teacher.dto.TeacherSignupDTO;
import com.ladysparks.ttaenggrang.domain.teacher.service.TeacherService;
import com.ladysparks.ttaenggrang.global.docs.teacher.TeacherAccountApiSpecification;
import com.ladysparks.ttaenggrang.global.response.ApiResponse;
import com.ladysparks.ttaenggrang.global.utill.SecurityUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/teachers")
@RequiredArgsConstructor
public class TeacherAccountController implements TeacherAccountApiSpecification {

    private final TeacherService teacherService;
    private final SecurityUtil securityUtil;

    // 교사 회원가입
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<TeacherSignupDTO>> signup(@RequestBody @Valid TeacherSignupDTO teacherSignupDTO) {  //  BindingResult bindingResult
//        // 1️⃣ 유효성 검사 실패 시, 에러 메시지 반환
//        if (bindingResult.hasErrors()) {
//            String errorMessage = bindingResult.getAllErrors().get(0).getDefaultMessage();
//            return ResponseEntity.badRequest().body(errorMessage);
//        }

        // 2️⃣ 회원가입 처리
        TeacherSignupDTO savedTeacher = teacherService.signupTeacher(teacherSignupDTO);
        return ResponseEntity.ok(ApiResponse.success(savedTeacher));
    }

    // 교사 로그인
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<TeacherLoginDTO>> login(@RequestBody @Valid TeacherLoginDTO teacherLoginDTO) {
        TeacherLoginDTO responseDTO = teacherService.loginTeacher(teacherLoginDTO);
        return ResponseEntity.ok(ApiResponse.success(responseDTO));
    }

    // 교사 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logoutTeacher(HttpServletRequest request) {
        // 1. 클라이언트로부터 Authorization 헤더에서 토큰 추출
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            // 2. (선택) JWT 블랙리스트 처리 또는 토큰 무효화 로직 추가 가능
            securityUtil.blacklistToken(token);

            return ResponseEntity.ok(ApiResponse.success("교사 로그아웃 성공"));
        } else {
            return ResponseEntity.badRequest().body(ApiResponse.error(400, "유효하지 않은 요청입니다.", null));
        }
    }

    // 교사 전체 목록 조회
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<TeacherResponseDTO>>> getAllTeachers() {
        ApiResponse<List<TeacherResponseDTO>> response = teacherService.getAllTeachers();
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}