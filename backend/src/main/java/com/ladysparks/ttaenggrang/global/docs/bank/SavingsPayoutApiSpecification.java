package com.ladysparks.ttaenggrang.global.docs.bank;

import com.ladysparks.ttaenggrang.domain.bank.dto.SavingsPayoutDTO;
import com.ladysparks.ttaenggrang.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "[학생] 적금 지급", description = "적금 만기/중도 해지 시 지급 내역 관리 API")
public interface SavingsPayoutApiSpecification {

    @Operation(summary = "(학생) 적금 지급 내역 [조회]", description = """
            💡 특정 적금 가입 정보에 대한 지급 내역을 조회합니다.
            
            ---
            
            **[ 요청 값 ]**
            - **savingsSubscriptionId** : 조회할 적금 가입 ID
            
            **[ 응답 필드 ]**
            - **id** : 적금 지급 ID
            - **savingsSubscriptionId** : 적금 가입 ID
            - **principalAmount** : 원금
            - **interestAmount** : 지급된 이자 금액
            - **payoutAmount** : 지급 금액
            - **payoutDate** : 지급일
            - **payoutType** : 지급 유형
                - 만기 지급 → **MATURITY**
                - 중도 인출 → **EARLY_WITHDRAWAL**
            - **paid** : 지급 여부
            - **createdAt** : 지급 내역 생성일
            
            ---
            
            **[ 설명 ]**
            - 지정된 `savingsSubscriptionId`에 대한 지급 내역을 조회합니다.
            - 지급 유형에는 `만기 지급(Maturity)`과 `중도 인출(Early Withdrawal)`이 포함됩니다.
            - 지급 받기를 클릭하면 `paid`가 true가 되고 은행에 지급 금액이 입금됩니다.
            """)
    ResponseEntity<ApiResponse<SavingsPayoutDTO>> savingsPayoutMatured(@Parameter(description = "조회할 적금 가입 ID", example = "1") @RequestParam Long savingsSubscriptionId);

    @Operation(summary = "(학생) 만기 지급 받기", description = """
        💡 학생이 만기된 적금에서 지급 받기를 클릭해서 지급 금액을 입금 받습니다.

        ---

        **[ 요청 값 ]**
        - **savingsSubscriptionId** : 적금 가입 내역 ID
        
        **[ 응답 필드 ]**
        - **id** : 지급 내역 ID
        - **savingsSubscriptionId** : 적금 가입 ID
        - **principalAmount** : 원금
        - **interestAmount** : 지급된 이자 금액
        - **payoutAmount** : 총 지급 금액 (원금 + 이자)
        - **payoutDate** : 지급일
        - **payoutType** : 지급 유형 (`MATURITY`: 만기 지급, `EARLY_WITHDRAWAL`: 중도 인출)
        - **isPaid** : 지급 완료 여부 (`true`: 지급 완료, `false`: 미지급)
        - **createdAt** : 지급 내역 생성일
        
        ---

        **[ 설명 ]**
        - `savingsSubscriptionId`에 해당하는 지급 내역의 `isPaid` 상태를 `true`로 변경합니다.
        - 이미 지급된 경우 예외가 발생합니다.
        - 응답으로는 적금 지급 내역을 반환합니다.
        """)
    ResponseEntity<ApiResponse<SavingsPayoutDTO>> savingsPayoutPaid(@RequestParam Long savingsPayoutId);

}
