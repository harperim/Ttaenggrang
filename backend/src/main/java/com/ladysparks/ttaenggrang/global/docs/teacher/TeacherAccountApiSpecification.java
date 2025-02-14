package com.ladysparks.ttaenggrang.global.docs.teacher;

import com.ladysparks.ttaenggrang.domain.teacher.dto.TeacherLoginDTO;
import com.ladysparks.ttaenggrang.domain.teacher.dto.TeacherResponseDTO;
import com.ladysparks.ttaenggrang.domain.teacher.dto.TeacherSignupDTO;
import com.ladysparks.ttaenggrang.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Tag(name = "[교사] 회원 계정", description = "교사 계정 관련 API")
public interface TeacherAccountApiSpecification {

    @Operation(summary = "(교사) 회원가입", description = """
            💡 회원가입을 진행합니다.
            
            - 교사만 접근 가능
            """)
    @PostMapping("/signup")
    ResponseEntity<ApiResponse<TeacherSignupDTO>> signup(@RequestBody TeacherSignupDTO teacherSignupDTO);

    @Operation(summary = "(교사) 로그인", description = "💡 교사의 로그인을 진행합니다.")
    @PostMapping("/login")
    ResponseEntity<ApiResponse<TeacherLoginDTO>> login(@RequestBody TeacherLoginDTO teacherLoginDTO);

    @Operation(summary = "(교사) 로그아웃", description = "💡 교사 계정을 로그아웃합니다.")
    @PostMapping("/logout")
    ResponseEntity<ApiResponse<String>> logoutTeacher(HttpServletRequest request);

    @Operation(summary = "(테스트용) 목록 조회", description = "💡 가입한 교사들의 목록을 조회합니다.")
    @PostMapping("/login")
    ResponseEntity<ApiResponse<List<TeacherResponseDTO>>> getAllTeachers();

}
