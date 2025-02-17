package com.ladysparks.ttaenggrang.domain.teacher.controller;

import com.ladysparks.ttaenggrang.domain.student.entity.Student;
import com.ladysparks.ttaenggrang.domain.student.repository.StudentRepository;
import com.ladysparks.ttaenggrang.domain.teacher.dto.*;
import com.ladysparks.ttaenggrang.domain.student.dto.StudentResponseDTO;
import com.ladysparks.ttaenggrang.domain.teacher.entity.Job;
import com.ladysparks.ttaenggrang.domain.teacher.entity.Teacher;
import com.ladysparks.ttaenggrang.domain.teacher.repository.TeacherRepository;
import com.ladysparks.ttaenggrang.domain.teacher.service.JobService;
import com.ladysparks.ttaenggrang.domain.teacher.service.NationService;
import com.ladysparks.ttaenggrang.domain.student.service.StudentService;
import com.ladysparks.ttaenggrang.global.docs.nation.TeacherFunctionApiSpecification;
import com.ladysparks.ttaenggrang.global.response.ApiResponse;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.antlr.v4.runtime.Token;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/teachers")
@RequiredArgsConstructor
public class TeacherFunctionController implements TeacherFunctionApiSpecification {

    private final JobService jobService;
    private final NationService nationService;
    private final StudentService studentService;
    private final TeacherRepository teacherRepository;
    private final StudentRepository studentRepository;

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

    // 현재 로그인한 사용자가 교사인지 학생인지 구분하여 teacherId 가져오기
    private Long getClassTeacherIdFromSecurityContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalArgumentException("인증되지 않은 사용자입니다. 로그인 후 다시 시도하세요.");
        }

        Object principalObj = authentication.getPrincipal();
        if (principalObj instanceof UserDetails) {
            String username = ((UserDetails) principalObj).getUsername();

            // ✅ 먼저 교사인지 확인
            Optional<Teacher> teacher = teacherRepository.findByEmail(username);
            if (teacher.isPresent()) {
                Long teacherId = teacher.get().getId();
                System.out.println("✅ 로그인한 사용자가 교사입니다. teacherId: " + teacherId);
                return teacherId;
            }

            // ✅ 교사가 아니라면 학생인지 확인
            Optional<Student> student = studentRepository.findByUsername(username);
            if (student.isPresent()) {
                Long classTeacherId = student.get().getTeacher().getId();  // 🔥 학생이 속한 교사의 ID 가져오기
                System.out.println("✅ 로그인한 사용자가 학생입니다. 해당 반의 teacherId: " + classTeacherId);
                return classTeacherId;
            }

            // ✅ 학생도 교사도 아닐 경우 예외 발생
            throw new IllegalArgumentException("해당 username을 가진 교사 또는 학생을 찾을 수 없습니다.");
        }

        throw new IllegalArgumentException("현재 인증된 사용자를 찾을 수 없습니다.");
    }

    // 현재 로그인한 학생ID 가져오는 메서드
    private Long getStudentIdFromSecurityContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principalObj = authentication.getPrincipal();
        if (principalObj instanceof UserDetails) {
            String username = ((UserDetails) principalObj).getUsername();
            Optional<Student> student = studentRepository.findByUsername(username);
            if (student.isPresent()) {
                return student.get().getId();
            }
        }
        throw new IllegalArgumentException("학생을 찾을 수 없습니다.");
    }


    // 직업 [등록]
    @PostMapping("/jobs/create")
    public ResponseEntity<ApiResponse<JobCreateDTO>> createJob(@RequestBody @Valid JobCreateDTO jobCreateDTO) {
        long teacherId = getTeacherIdFromSecurityContext();

        ApiResponse<JobCreateDTO> response = jobService.createJob(jobCreateDTO, teacherId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    // 직업 [해당 직업을 가진 우리 반 학생 목록 조회]
    @GetMapping("/jobs/{jobId}")
    public ResponseEntity<ApiResponse<List<StudentResponseDTO>>> getStudentsByJobIdAndTeacher(@PathVariable Long jobId) {
        long teacherId = getTeacherIdFromSecurityContext();

        ApiResponse<List<StudentResponseDTO>> response = studentService.getStudentsByJobIdAndTeacher(teacherId, jobId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    // 우리 반 직업 정보 [조회]
    @GetMapping("/jobs/class")
    public ResponseEntity<ApiResponse<List<JobClassDTO>>> getClassJobs() {
        long teacherId = getTeacherIdFromSecurityContext();

        ApiResponse<List<JobClassDTO>> response = jobService.getClassJobs(teacherId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    // 국가 정보 [등록]
    @PostMapping("/nations")
    public ResponseEntity<ApiResponse<NationDTO>> createNation(@RequestBody @Valid NationDTO nationDTO) {

        // 로그인한 교사의 ID 가져오기
        Long teacherId = getTeacherIdFromSecurityContext();

        ApiResponse<NationDTO> response = nationService.createNation(teacherId, nationDTO);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    // 국가 정보 [조회]
    @GetMapping("/nations")
    public ResponseEntity<ApiResponse<NationDTO>> getNationByTeacher() {
        // 현재 로그인한 교사 ID 가져오기
        long teacherId = getClassTeacherIdFromSecurityContext();

        ApiResponse<NationDTO> response = nationService.getNationByTeacherId(teacherId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    // 국가 정보 [삭제]
    @DeleteMapping("/nations")
    public ResponseEntity<ApiResponse<Void>> deleteNation() {
        Long teacherId = getTeacherIdFromSecurityContext();

        ApiResponse<Void> response = nationService.deleteNation(teacherId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    // 직업 [수정]
    @PutMapping("/jobs/{studentId}")
    public ResponseEntity<ApiResponse<StudentJobUpdateResponseDTO>> updateStudentJob(
            @PathVariable Long studentId,
            @RequestBody StudentJobUpdateDTO jobUpdateDTO) {

        Long teacherId = getTeacherIdFromSecurityContext();
        ApiResponse<StudentJobUpdateResponseDTO> response = studentService.updateStudentJob(studentId, jobUpdateDTO, teacherId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}
