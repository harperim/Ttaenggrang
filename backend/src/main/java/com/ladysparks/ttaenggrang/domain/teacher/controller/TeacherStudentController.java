package com.ladysparks.ttaenggrang.domain.teacher.controller;

import com.ladysparks.ttaenggrang.domain.teacher.dto.MultipleStudentCreateDTO;
import com.ladysparks.ttaenggrang.domain.teacher.dto.SingleStudentCreateDTO;
import com.ladysparks.ttaenggrang.domain.student.dto.StudentResponseDTO;
import com.ladysparks.ttaenggrang.domain.teacher.repository.TeacherRepository;
import com.ladysparks.ttaenggrang.domain.student.service.StudentService;
import com.ladysparks.ttaenggrang.global.docs.TeacherStudentApiSpecificaion;
import com.ladysparks.ttaenggrang.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/teachers")
@RequiredArgsConstructor
public class TeacherStudentController implements TeacherStudentApiSpecificaion {

    private final TeacherRepository teacherRepository;
    private final StudentService studentService;

    // 학생 계정 빠른 생성 (교사만 가능)  (토큰 문제 해결 후 다시 사용하기)
    @PostMapping(value = "/quick-create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<List<StudentResponseDTO>>> createStudents(
            @RequestParam("baseId") String baseId,
            @RequestParam("studentCount") int studentCount,
            @RequestPart(required = false) MultipartFile file) {  // 🔥 파일과 함께 데이터를 받으려면 ModelAttribute 사용

        // ✅ 현재 로그인한 교사의 ID 가져오기
        Long teacherId = getTeacherIdFromSecurityContext();

        // DTO로 변환
        MultipleStudentCreateDTO studentCreateDTO = new MultipleStudentCreateDTO();
        studentCreateDTO.setBaseId(baseId);
        studentCreateDTO.setStudentCount(studentCount);
        studentCreateDTO.setFile(file);

        List<StudentResponseDTO> createdStudents = studentService.createStudentAccounts(teacherId, studentCreateDTO);

        return ResponseEntity.ok(ApiResponse.success(createdStudents));
    }

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

    // 학생 계정 단일 생성 (교사만 가능)
    @PostMapping("/single-create")
    public ResponseEntity<ApiResponse<StudentResponseDTO>> createStudent(@RequestBody SingleStudentCreateDTO studentCreateDTO) {
        // 1. 현재 로그인한 교사의 ID 가져오기
        Long teacherId = getTeacherIdFromSecurityContext();

        // 2. 학생 계정 생성 서비스 호출
        ApiResponse<StudentResponseDTO> createdStudent = studentService.createStudent(teacherId, studentCreateDTO);

        // 3. 생성된 학생 정보 반환
        return ResponseEntity.status(createdStudent.getStatusCode()).body(createdStudent);
    }

    // 우리반 학생 전체 조회
    @GetMapping("/students")
    public ResponseEntity<ApiResponse<List<StudentResponseDTO>>> getMyClassStudents() {
        Long teacherId = getTeacherIdFromSecurityContext(); // 🔥 로그인한 교사의 ID 가져오기
        ApiResponse<List<StudentResponseDTO>> response = studentService.getMyClassStudents(teacherId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    // 우리반 특정 학생 상세 조회
    @GetMapping("/students/{studentId}")
    public ResponseEntity<ApiResponse<StudentResponseDTO>> getStudentById(@PathVariable Long studentId) {
        Long teacherId = getTeacherIdFromSecurityContext(); // 🔥 로그인한 교사의 ID 가져오기
        ApiResponse<StudentResponseDTO> response = studentService.getStudentById(teacherId, studentId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}
