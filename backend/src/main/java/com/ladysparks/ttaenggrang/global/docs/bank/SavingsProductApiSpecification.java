package com.ladysparks.ttaenggrang.global.docs.bank;

import com.ladysparks.ttaenggrang.domain.bank.dto.SavingsProductDTO;
import com.ladysparks.ttaenggrang.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Tag(name = "[교사/학생] 적금 상품", description = "적금 상품 관련 API")
public interface SavingsProductApiSpecification {

    @Operation(summary = "(교사) 적금 상품 [등록]", description = """
            💡 교사가 적금 상품을 등록합니다.
            
            ---

            **[ 요청 필드 ]**
            - **name** : 적금 상품명
            - **interestRate** : 이자율
            - **earlyInterestRate** : 중도 해지시 적용되는 이자율
            - **durationWeeks** : 가입 기간 (주 단위)
            - **amount** : 적금 금액 (주마다 납입하는 금액)
            - **saleStartDate** : 노출 시작일
            - **saleEndDate** : 노출 종료일
            
            ---
            
            **[ 설명 ]**
            - 같은 교사가 같은 이름의 적금 상품을 등록할 수 없습니다.
            - 이자율의 범위는 0.0 ~ 100.0 (%)
            """)
    ResponseEntity<ApiResponse<SavingsProductDTO>> savingsProductAdd(SavingsProductDTO savingsProductDTO);

    @Operation(summary = "(교사/학생) 적금 상품 목록 [조회]", description = """
            💡 전체 적금 상품 목록을 조회합니다.
            
            ---

            **[ 응답 필드 ]**
            - **name** : 적금 상품명
            - **interestRate** : 이자율
            - **earlyInterestRate** : 중도 해지시 적용되는 이자율
            - **durationWeeks** : 가입 기간 (주 단위)
            - **amount** : 적금 금액
            - **saleStartDate** : 노출 시작일
            - **saleEndDate** : 노출 종료일
            
            ---
            
            **[ 설명 ]**
            - 교사로 로그인할 경우 해당 교사가 등록한 적금 상품 목록을 조회합니다.
            - 학생으로 로그인한 경우 학생을 관리하는 교사가 등록한 적금 상품 목록을 조회합니다.
            """)
    ResponseEntity<ApiResponse<List<SavingsProductDTO>>> savingsProductList();

}
