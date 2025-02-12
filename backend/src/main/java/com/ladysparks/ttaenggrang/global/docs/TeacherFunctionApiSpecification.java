package com.ladysparks.ttaenggrang.global.docs;

import com.ladysparks.ttaenggrang.domain.teacher.dto.JobClassDTO;
import com.ladysparks.ttaenggrang.domain.teacher.dto.NationDTO;
import com.ladysparks.ttaenggrang.domain.student.dto.StudentResponseDTO;
import com.ladysparks.ttaenggrang.domain.teacher.dto.JobCreateDTO;
import com.ladysparks.ttaenggrang.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.mapstruct.SubclassMapping;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Tag(name = "Teacher-Function", description = "교사 관리 기능 API")
public interface TeacherFunctionApiSpecification {

    @Operation(summary = "직업 [등록]", description = """
            💡 교사가 새로운 직업을 등록합니다.
            
            - **jobName** : 직업명
            - **jobDescription** : 수행할 역할을 설명합니다.
            - **baseSalary** : 기본급
            - **maxPeople** : 해당 직업을 가질 수 있는 최대 인원 수
            """)
    ResponseEntity<ApiResponse<JobCreateDTO>> createJob(@RequestBody @Valid JobCreateDTO jobCreateDTO);

    @Operation(summary = "국가 [등록]", description = """
            💡 교사가 국가 정보를 등록합니다.
            
            **[ 필드 설명 ]**
            - **nationName** : 국가 이름
            - **population** : 인구 수 (학생 수)
            - **currency** : : 통화 단위
            - **savingsGoalAmount** : 학급 별 목표 저축액
            - **nationalTreasury** : 국고
            - **establishedDate** : 설립일 (국가 정보 등록한 날짜로 자동 생성)
            
            **[ 규칙 ]**
            - 계정 당 1개의 국가만 생성할 수 있습니다.
            - 만약 다른 국가를 개설하고 싶다면 기존 국가 정보를 삭제해야 합니다.
            """)
    ResponseEntity<ApiResponse<NationDTO>> createNation(@RequestBody @Valid NationDTO nationDTO);

    @Operation(summary = "국가 [조회]", description = """
            💡 교사가 국가 정보를 조회합니다.
            
            - **nationName** : 국가명
            - **population** : 인구 수 (학생 수)
            - **currency** : 통화 단위
            - **savingsGoalAmount** : 학급 별 목표 저축액
            - **nationalTreasury** : 국고
            - **establishedDate** : 설립일 (국가 정보 등록한 날짜로 자동 생성)
            """)
    ResponseEntity<ApiResponse<NationDTO>> getNationByTeacher();

    @Operation(summary = "국가 [삭제]", description = "💡 교사가 국가 정보를 삭제합니다.")
    ResponseEntity<ApiResponse<Void>> deleteNation();

    @Operation(summary = "직업 [학생 전체 조회]", description = """
            💡 직업 ID를 입력하면 해당 직업을 가진 우리반 학생들을 조회할 수 있습니다.
            
            - **id** : 직업 고유 ID
            - **username** : 학생 ID
            - **name** : 학생 실명
            - **profileImage** : 학생 프로필 이미지 경로
            - **teacher** : 학생의 담임 선생님 정보
            - **bankAccount** : 학생의 계좌 정보
            - **token : 학생 로그인 시 토큰 값
            """)
    ResponseEntity<ApiResponse<List<StudentResponseDTO>>> getStudentsByJobIdAndTeacher(@PathVariable Long jobId);

    @Operation(summary = "우리 반 직업 정보 [조회]", description = """
            💡 우리 반에서 사용하고 있는 직업 정보를 조회합니다.
            """)
    ResponseEntity<ApiResponse<List<JobClassDTO>>> getClassJobs();

}
