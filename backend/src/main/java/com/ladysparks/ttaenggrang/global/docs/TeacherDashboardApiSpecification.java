package com.ladysparks.ttaenggrang.global.docs;

import com.ladysparks.ttaenggrang.domain.bank.dto.StudentDailyAverageFinancialDTO;
import com.ladysparks.ttaenggrang.domain.teacher.dto.StudentManagementDTO;
import com.ladysparks.ttaenggrang.domain.teacher.dto.TeacherDashboardDTO;
import com.ladysparks.ttaenggrang.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Tag(name = "Teacher-Dashboard", description = "교사 대시보드 관련 API")
public interface TeacherDashboardApiSpecification {

    @Operation(summary = "학생들의 최근 7일 평균 수입 및 지출 [조회]", description = """
            
            💡 교사가 담당하는 학생들의 최근 7일의 평균 수입과 평균 지출을 반환합니다.
            
            **[ 응답 필드 ]**
            - **date** : 날짜
            - **averageIncome** : 평균 수입
            - **averageExpense** : 평균 지출
            """)
    ResponseEntity<ApiResponse<List<StudentDailyAverageFinancialDTO>>> dailyAverageIncomeAndExpenseDetails();

    @Operation(summary = "교사 메인 화면 대시보드 [조회]", description = """
            
            💡 교사 메인 화면 대시보드를 구성하는 데이터 정보를 조회합니다.

            **[ 응답 필드 ]**
            - **treasuryIncome** : 국고 수입
            - **averageStudentBalance** : 1인 평균 잔고
            - **activeItemCount** : 판매 중인 상품 개수
            - **classSavingsGoal** : 우리 반 목표 저축액
            
            """)
    ResponseEntity<ApiResponse<TeacherDashboardDTO>> teacherDashboardDetails();

    @Operation(summary = "학생 관리 내역 [조회]", description = """
            💡 특정 교사가 담당하는 학생들의 관리 내역을 조회합니다.
            
            ---
            
            **[ 응답 필드 ]**
            - **studentId** : 학생 ID
            - **studentName** : 학생 이름
            - **username** : 학생 계정 (username)
            - **jobName** : 학생의 현재 직업
            - **baseSalary** : 직업 기본 월급
            - **accountBalance** : 학생 계좌 잔고
            
            ---
            
            **[ 동작 방식 ]**
            - 현재 로그인한 교사의 `teacherId` 를 기반으로 학생 관리 내역을 조회합니다.
            - 학생의 기본 정보(studentName, username), 직업 정보 및 월급, 계좌 잔액을 반환합니다.
            - 직업이 없는 경우 ""(빈 문자열)을 반환합니다.
            """)
    ResponseEntity<ApiResponse<List<StudentManagementDTO>>> studentManagementDetails();
}
