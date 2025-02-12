package com.ladysparks.ttaenggrang.global.docs;

import com.ladysparks.ttaenggrang.domain.bank.dto.SavingsDepositDTO;
import com.ladysparks.ttaenggrang.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Tag(name = "Savings-Deposit", description = "적금 납입 내역 관련 API")
public interface SavingsDepositApiSpecification {

    @Operation(summary = "적금 납입 [등록]", description = """
            💡 학생이 미납된 적금을 수동으로 납입합니다.

            **[ 요청 필드 ]**
            - **savingsDepositId** : 적금 납입 정보 ID
            
            **[ 규칙 ]**
            - 학생이 적금 가입 시 선택한 요일마다 납입합니다. (주 1회 자동 납입)
            - 납입 예정일에 은행 계좌 잔액 부족으로 미납된 경우, 이후 학생이 수동으로 납입합니다.
            """)
    ResponseEntity<ApiResponse<SavingsDepositDTO>> savingsDepositRetry(@PathVariable Long savingsDepositId);

    @Operation(summary = "적금 납입 내역 [조회]", description = """
            💡 학생의 적금 납입 내역을 조회합니다.
          
            **[ 응답 필드 ]**
            - **id** : 적금 납입 정보 ID
            - **savingsSubscriptionId** : 적금 가입 정보 ID
            - **amount** : 납입 금액
            - **scheduledDate** : 납입 예정일
            - **status** : 납입 상태
                - **"PENDING"** : 예정됨 (예정일 전)
                - **"COMPLETED"** : 납입 완료
                - **"FAILED"** : 미납 (잔액 부족)
            - **createdAt** : 납입 정보 생성일
            - **updatedAt** : 납입 정보 수정일
            """)
    ResponseEntity<ApiResponse<List<SavingsDepositDTO>>> savingsDepositList(@RequestParam Long savingsSubscriptionId);

    @Operation(summary = "적금 미납 내역 [조회]", description = """
            💡 학생의 적금 미납 내역을 조회합니다.

            **[ 응답 필드 ]**
            - **id** : 적금 납입 정보 ID
            - **savingsSubscriptionId** : 적금 가입 정보 ID
            - **amount** : 납입 금액
            - **scheduledDate** : 납입 예정일
            - **status** : 납입 상태
                - **"PENDING"** : 예정됨 (예정일 전)
                - **"COMPLETED"** : 납입 완료
                - **"FAILED"** : 미납 (잔액 부족)
            - **createdAt** : 납입 정보 생성일
            - **updatedAt** : 납입 정보 수정일
            """)
    ResponseEntity<ApiResponse<List<SavingsDepositDTO>>> savingsDepositsFailedList();
}
