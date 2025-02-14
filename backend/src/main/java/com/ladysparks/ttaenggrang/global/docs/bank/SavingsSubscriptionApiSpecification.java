package com.ladysparks.ttaenggrang.global.docs.bank;

import com.ladysparks.ttaenggrang.domain.bank.dto.DepositAndSavingsCountDTO;
import com.ladysparks.ttaenggrang.domain.bank.dto.SavingsSubscriptionDTO;
import com.ladysparks.ttaenggrang.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Tag(name = "[학생] 적금 가입", description = "적금 가입 내역 관련 API")
public interface SavingsSubscriptionApiSpecification {

    @Operation(summary = "(학생) 적금 가입 [등록]", description = """
            💡 학생이 적금 상품에 가입합니다.
            
            ---
            
            **[ 요청 필드 ]**
            - **savingsProductId** : 적금 상품 ID
            - **depositDayOfWeek** : 납입 요일
                - **"MONDAY"**
                - **"TUESDAY"**
                - **"WEDNESDAY"**
                - **"THURSDAY"**
                - **"FRIDAY"**
                - **"SATURDAY"**
                - **"SUNDAY"**

            ---
            
            **[ 설명 ]**
            - 학생이 선택한 요일에 자동으로 적금을 납입합니다.
            - durationWeeks, interestRate, amount, payoutAmount 값이 안보임 → 적금 가입 내역 API에서는 정상적으로 보임(등록 응답에서는 안보여도 되지 않을까요..?)
            """)
    ResponseEntity<ApiResponse<SavingsSubscriptionDTO>> savingsSubscriptionAdd(@RequestBody SavingsSubscriptionDTO savingsSubscriptionDTO);

    @Operation(summary = "(학생) 적금 가입 내역 [전체 조회]", description = """
            💡 학생의 적금 가입 내역을 조회합니다.
            
            ---
            
            **[ 응답 필드 ]**
            - **id** : 적금 가입 ID
            - **savingsProductId** : 적금 상품 ID
            - **studentId** : 학생 ID
            - **durationWeeks** : 가입 기간
            - **interestRate* : 이자율
            - **amount** : 매주 납입 금액
            - **startDate** : 납입 시작일
            - **endDate** : 납입 종료일
            - **status** : 가입 상태
            - **depositDayOfWeek** : 납입 요일
                - **"MONDAY"**
                - **"TUESDAY"**
                - **"WEDNESDAY"**
                - **"THURSDAY"**
                - **"FRIDAY"**
                - **"SATURDAY"**
                - **"SUNDAY"**
            - **payoutAmount** : 예상 지급액
            - **createdAt** : 적금 가입일
            - **depositSchedule** : 납입 일정 (단순 확인용)
            """)
    ResponseEntity<ApiResponse<List<SavingsSubscriptionDTO>>> savingsSubscriptionList();

    @Operation(summary = "(학생) 적금/예금 상품 가입 현황 [조회]", description = """
            💡 특정 학생이 가입 중인 예금 및 적금 상품의 개수를 조회합니다.
            
            ---
             **[ 응답 필드 ]**
            - **depositProductCount** : 예금 상품 개수
            - **savingsProductCount** : 적금 상품 개수
            
            ---
            
            **[ 설명 ]**
            - 현재 로그인한 학생 ID(`studentId`)를 기준으로 해당 학생이 가입한 예금/적금 상품 개수를 반환합니다.
            """)
    @GetMapping("/savings-count")
    ResponseEntity<ApiResponse<DepositAndSavingsCountDTO>> getStudentSavingsCount();

}