package com.ladysparks.ttaenggrang.global.docs.student;

import com.ladysparks.ttaenggrang.domain.student.dto.StudentJobResponseDTO;
import com.ladysparks.ttaenggrang.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "[학생] 내 정보 관리", description = "학생 정보 관련 API")
public interface StudentApiSpecification {

    @Operation(summary = "(학생) 직업 정보 조회", description = "💡 학생이 로그인 시, 자신의 직업명, 기본 급여를 조회합니다.")
    @GetMapping("/job")
    ApiResponse<StudentJobResponseDTO> getStudentJob();

}
