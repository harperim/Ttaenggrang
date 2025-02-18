package com.ladysparks.ttaenggrang.domain.student.controller;

import com.ladysparks.ttaenggrang.domain.student.dto.StudentLoginRequestDTO;
import com.ladysparks.ttaenggrang.domain.student.dto.StudentLoginResponseDTO;
import com.ladysparks.ttaenggrang.domain.student.dto.StudentResponseDTO;
import com.ladysparks.ttaenggrang.domain.student.entity.Student;
import com.ladysparks.ttaenggrang.domain.student.repository.StudentRepository;
import com.ladysparks.ttaenggrang.domain.student.service.StudentService;
import com.ladysparks.ttaenggrang.domain.teacher.dto.JobInfoDTO;
import com.ladysparks.ttaenggrang.global.docs.student.StudentAccountApiSpecification;
import com.ladysparks.ttaenggrang.global.response.ApiResponse;
import com.ladysparks.ttaenggrang.global.utill.SecurityUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/students")
@RequiredArgsConstructor
public class StudentAccountController implements StudentAccountApiSpecification {

    private final StudentService studentService;
    private final SecurityUtil securityUtil;
    private final StudentRepository studentRepository;

    // 학생 로그인
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<StudentLoginResponseDTO>> loginStudents(@RequestBody @Valid StudentLoginRequestDTO studentLoginDTO) {
        StudentLoginResponseDTO responseDTO = studentService.loginStudent(studentLoginDTO);
        return ResponseEntity.ok(ApiResponse.success(responseDTO));
    }

    // 학생 전체 목록 조회
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<StudentResponseDTO>>> getAllStudents() {
        ApiResponse<List<StudentResponseDTO>> response = studentService.getAllStudents();
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logoutStudent(HttpServletRequest request) {
        // 1. 클라이언트로부터 Authorization 헤더에서 토큰 추출
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            // 2. (선택) JWT 블랙리스트 처리 또는 토큰 무효화 로직 추가 가능
            securityUtil.blacklistToken(token);

            return ResponseEntity.ok(ApiResponse.success("학생 로그아웃 성공"));
        } else {
            return ResponseEntity.badRequest().body(ApiResponse.error(400, "유효하지 않은 요청입니다.", null));
        }
    }
}
