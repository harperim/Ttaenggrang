package com.ladysparks.ttaenggrang.global.docs.bank;

import com.ladysparks.ttaenggrang.domain.bank.dto.BankAccountDTO;
import com.ladysparks.ttaenggrang.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "[학생] 은행 계좌", description = "은행 계좌 관련 API")
public interface BankAccountApiSpecification {

    /*
    @Operation(summary = "은행 계좌 [등록]", description = "학생 계정 생성 전에 계좌 생성(→ API 하나로 합치는 게 좋을 듯)")
    ResponseEntity<ApiResponse<BankAccountDTO>> BankAccountAdd(@RequestBody BankAccountDTO bankAccountDto);
    */

    @Operation(summary = "(학생) 은행 계좌 [조회]", description = """
            💡 학생의 은행 계좌 정보를 조회합니다.
            
            ---

            **[ 응답 필드 ]**
            - **id** : 은행 계좌 ID
            - **accountNumber** : 계좌 번호
            - **balance** : 계좌 잔액
            - **createdAt**: 계좌 생성일
            """)
    ResponseEntity<ApiResponse<BankAccountDTO>> BankAccountDetails();

}
