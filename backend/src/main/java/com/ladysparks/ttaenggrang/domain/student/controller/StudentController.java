package com.ladysparks.ttaenggrang.domain.student.controller;

import com.ladysparks.ttaenggrang.domain.student.dto.StudentJobResponseDTO;
import com.ladysparks.ttaenggrang.domain.student.entity.Student;
import com.ladysparks.ttaenggrang.domain.student.repository.StudentRepository;
import com.ladysparks.ttaenggrang.domain.student.service.StudentService;
import com.ladysparks.ttaenggrang.global.docs.student.StudentApiSpecification;
import com.ladysparks.ttaenggrang.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/students")
public class StudentController implements StudentApiSpecification {

    private final StudentService studentService;
    private final StudentRepository studentRepository;

    // (+) 현재 로그인한 학생의 ID 가져오는 메서드
    private long getStudentIdFromSecurityContext() {
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
            String username = ((UserDetails) principalObj).getUsername();
            return studentRepository.findByUsername(username)
                    .orElseThrow(() -> new IllegalArgumentException("해당 ID를 가진 학생을 찾을 수 없습니다."))
                    .getId();
        }
        throw new IllegalArgumentException("현재 인증된 사용자를 찾을 수 없습니다.");
    }

    @GetMapping("/job")
    public ApiResponse<StudentJobResponseDTO> getStudentJob() {
        Long studentId = getStudentIdFromSecurityContext();
        StudentJobResponseDTO studentJobResponseDTO = studentService.getStudentJobSalary(studentId);
        return ApiResponse.success(studentJobResponseDTO);
    }
}
