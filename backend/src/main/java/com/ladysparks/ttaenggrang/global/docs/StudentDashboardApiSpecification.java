package com.ladysparks.ttaenggrang.global.docs;

import com.ladysparks.ttaenggrang.domain.student.dto.BankTransactionSummaryDTO;
import com.ladysparks.ttaenggrang.domain.student.dto.StudentAssetDTO;
import com.ladysparks.ttaenggrang.domain.student.dto.StudentDashboardDTO;
import com.ladysparks.ttaenggrang.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Tag(name = "Student-Dashboard", description = "학생 대시보드 관련 API")
public interface StudentDashboardApiSpecification {

    @Operation(summary = "학생 메인 화면 대시보드 [조회]", description = """
            💡학생 메인 화면 대시보드를 구성하는 데이터 정보를 조회합니다.
            
            ---
            
            **[ 응답 필드 ]**
            - **studentId** : 학생 ID
            - **accountBalance** : 계좌 잔액
            - **currentRank** : 내 순위
            - **totalSavings** : 총 저축 (적금 납입액)
            - **totalInvestmentAmount** : 투자 평가액
            - **totalAsset** : 총 자산 (적금 납입액 + 계좌 잔액 + 투자 평가액)
            - **goalAmount** : 목표액
            - **achievementRate** : 목표 달성률 (%) ( `(총 자산 / 목표액) * 100` )
            
            ---
            
            **[ 동작 방식 ]**
            - 현재 로그인한 학생의 `studentId` 를 기반으로 정보를 조회합니다.
            - 학생의 **계좌 잔액, 현재 순위, 적금 납입액, 투자 평가액, 목표액** 등을 불러옵니다.
            - 목표 달성률은 `(총 자산 / 목표액) * 100` 으로 계산됩니다.
            
            """)
    ResponseEntity<ApiResponse<StudentDashboardDTO>> studentDashboardDetails();

    @Operation(summary = "학생 자산 정보 [조회]", description = """
            💡 학생 자산 정보를 조회하여 금융 상태를 한눈에 파악할 수 있습니다.

            ---

            **[ 응답 필드 ]**
            - **studentId** : 학생 ID
            - **totalAsset** : 총 자산 (계좌 잔액 + 저축 + 투자 수익)
            - **totalSalary** : 총 급여
            - **totalSavings** : 총 저축 (적금 납입 중인 금액 + 적금 만기/중도 인출로 인해 지급받은 금액)
            - **totalInvestmentProfit** : 총 투자 수익 (매도 금액 + 투자 평가액)
            - **totalIncentive** : 총 인센티브
            - **totalExpense** : 총 소비
            
            ---

            **[ 동작 방식 ]**
            - 현재 로그인한 학생의 금융 데이터를 조회합니다.
            - `적금 납입 중인 금액`, `만기/중도 인출 지급된 금액`을 합쳐 `총 저축`으로 계산합니다.
            - `매도 금액`과 `투자 평가액`을 합쳐 `총 투자 수익`으로 반환합니다.
            """)
    ResponseEntity<ApiResponse<StudentAssetDTO>> studentDashboardAsset();

    @Operation(summary = "학생의 거래 내역 [조회]", description = """
        💡 현재 로그인한 학생의 거래 내역을 최신순으로 조회합니다.

        ---
        
        **[ 응답 필드 ]**
        - **transactionDate** : 거래 날짜
        - **transactionType** : 거래 내역 (거래 타입)
            - 입금 → **DEPOSIT**
            - 출금 → **WITHDRAW**
            - 송금 → **TRANSFER**
            - 아이템 구매/판매 → **ITEM**
            - 주식 매수 → **STOCK_BUY**
            - 주식 매도 → **STOCK_SELL**
            - ETF 매수 → **ETF_BUY**
            - ETF 매도 → **ETF_SELL**
            - 적금 납입 → **SAVINGS_DEPOSIT**
            - 적금 이자 수령 → **SAVINGS_INTEREST**
            - 은행 계좌 이자 수령 → **BANK_INTEREST**
            - 급여 수령 → **SALARY**
            - 인센티브 수령 → **INCENTIVE**
            - 세금 납부 → **TAX**
            - 벌금 납부 → **FINE**
        - **amount** : 거래 금액
        - **accountBalance** : 거래 후 계좌 잔고

        ---
         **[ 동작 방식 ]**
        - 학생은 자신의 계좌 내역만 조회할 수 있습니다.
        """)
    @GetMapping
    public ResponseEntity<ApiResponse<List<BankTransactionSummaryDTO>>> getStudentTransactionSummaryList();

//    @Operation(summary = "학생의 저축 목표 달성률 [조회]", description = """
//            💡 특정 학생의 학급 내 저축 목표 달성률 및 목표 달성 순위를 조회합니다.
//
//            **[ 응답 필드 ]**
//            - **studentId** : 학생 ID
//            - **savingsAchievementRate** : 저축 목표 달성률 (단위: %)
//            - **rank** : 학생의 목표 달성 순위 (1위부터 시작)
//
//            **[ 동작 방식 ]**
//            - 먼저 Redis에서 목표 달성률을 조회합니다.
//            - 만약 Redis에 값이 없으면 DB에서 계산 후 Redis에 저장합니다.
//            """)
//    ResponseEntity<ApiResponse<SavingsAchievementDTO>> teacherDashboardDetails();

//    @Operation(summary = "목표 달성률 기준 TOP N 학생 목록 [조회]", description = """
//            💡 목표 달성률을 기준으로 학급 내 상위 N명의 학생을 조회합니다.
//            - N = 0인 경우, 전체 학생 순위 조회
//
//            **[ 요청 필드 ]**
//            - **topN** : 조회할 상위 N명의 학생 수 (필수)
//
//            **[ 응답 필드 ]**
//            - **studentId** : 학생 ID
//            - **goalAchievement** : 목표 달성률 (%)
//            - **rank** : 학생의 목표 달성 순위 (1위부터 시작)
//            """)
//    ResponseEntity<ApiResponse<List<SavingsAchievementDTO>>> TopStudentList(@RequestParam int topN);

}
