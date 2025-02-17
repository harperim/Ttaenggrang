package com.ladysparks.ttaenggrang.global.docs.weekly;

import com.ladysparks.ttaenggrang.domain.weekly_report.dto.StudentFinancialSummaryDTO;
import com.ladysparks.ttaenggrang.domain.weekly_report.dto.WeeklyFinancialSummaryDTO;
import com.ladysparks.ttaenggrang.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "[학생] 주간 통계 보고서", description = "주간 금융 리포트 관련 API")
public interface WeeklyReportApiSpecification {

    @Operation(summary = "(학생) 이번 주 금융 활동 요약 [조회]", description = """
            💡 특정 학생의 이번주 금융 활동을 분석하여 주간 리포트를 제공합니다.
            
            ---
    
            **[ 응답 필드 ]**
            - **studentId** : 학생 ID
            - **reportDate** : 주간 통계 생성일
            - **totalIncome** : 총 수입 (입금, 급여, 아이템 판매, 주식·ETF 매도, 적금 이자, 은행 이자, 인센티브)
            - **salaryAmount** : 급여
            - **savingsAmount** : 총 저축(적금 납입)
            - **investmentReturn** : 투자 수익
            - **incentiveAmount** : 인센티브
            - **totalExpenses** : 총 소비 (출금, 아이템 구매, 주식·ETF 매수, 적금 납입)
            - **taxAmount** : 세금 납부액
            - **fineAmount** : 벌금 납부액
            """)
    ResponseEntity<ApiResponse<WeeklyFinancialSummaryDTO>> weeklyReportDetails();

    @Operation(summary = "(학생) 이번주 내 금융 성적표 [조회]", description = """
           💡 특정 학생의 **지난주, 이번주, 반 평균** 저축 증가율, 투자 수익율, 지출 증가율을 반환합니다.
           
           ---
    
           **[ 응답 필드 ]**
           - **lastWeekSummary**: 해당 학생의 지난주 저축 증가율, 투자 수익율, 지출 증가율
           - **thisWeekSummary**: 해당 학생의 이번주 저축 증가율, 투자 수익율, 지출 증가율
           - **classAverageSummary**: 해당 학생이 속한 반의 평균 저축 증가율, 투자 수익율, 지출 증가율
           """)
    ResponseEntity<ApiResponse<StudentFinancialSummaryDTO>> weeklyReportGrowthList();

    @Operation(summary = "(학생) 최신 AI 피드백 [조회]", description = """
        💡 특정 학생의 가장 최신 주간 AI 피드백을 조회합니다.

        ---

        **[ 요청 값 ]**
        - `studentId`: 조회할 학생의 ID

        **[ 응답 필드 ]**
        - `aiFeedback` : AI 피드백 내용

        ---

        **[ 설명 ]**
        - 해당 학생의 최신 주간 리포트에서 AI 피드백만 가져옵니다.
        - 데이터가 없을 경우 `"AI 피드백이 없습니다."` 메시지를 반환합니다.
        """)
    ResponseEntity<ApiResponse<String>> getLatestAIFeedback();

}