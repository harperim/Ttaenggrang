package com.ladysparks.ttaenggrang.global.docs.bank;

import com.ladysparks.ttaenggrang.domain.bank.dto.BankTransactionDTO;
import com.ladysparks.ttaenggrang.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Tag(name = "[학생] 은행 계좌 거래", description = "은행 계좌 거래 내역 관련 API")
public interface BankTransactionApiSpecification {

    @Operation(summary = "(학생) 은행 계좌 거래 [등록]", description = """
            💡 은행 계좌 거래를 진행합니다.
            
            ---

            **[ 요청 필드 ]**
            - **type** : 거래 타입
                - 입금 → **DEPOSIT**
                - 출금 → **WITHDRAW**
                - 송금 → **TRANSFER**
                - 아이템 구매 → **ITEM_BUY**
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
            - **description** : 거래 내용
            - **receiverId** (선택) : 거래 대상 학생 ID (입금 받는 학생)
            
            ---
            
            **[ 설명 ]**
            - 은행 계좌 거래 시 자동으로 해당 은행 계좌 잔액에 반영됩니다.
            - 거래 타입이 **TRANSFER**, **ITEM**인 경우, receiverId에 거래 대상 학생 ID를 포함해야 합니다.
                - 송금, 아이템 거래 시 두 계좌에서 거래가 발생해야 합니다. (총 2건의 등록 발생 -> 백엔드에서 구현)
                - ex. 송금 → 송금인은 출금, 수취인은 입금
                - ex. 아이템 거래 → 구매자는 출금, 판매자는 입금
            - 세금, 벌금 납부 시 해당 금액이 국고에 합산되어야 합니다. (이건 백엔드에서 구현)
            """)
    ResponseEntity<ApiResponse<BankTransactionDTO>> bankTransactionAdd(@RequestBody BankTransactionDTO bankTransactionDTO);

    @Operation(summary = "(학생) 은행 계좌 거래 내역 [전체 조회]", description = """
            💡 학생의 은행 계좌 거래 내역을 조회합니다.
            
            ---

            **[ 응답 필드 ]**
            - **id** : 은행 계좌 거래 ID
            - **bankAccountId** : 은행 계좌 ID
            - **type** : 거래 타입
            - **amount** : 거래 금액
            - **balanceBefore** : 거래 전 잔액
            - **balanceAfter** : 거래 후 잔액
            - **description** : 거래 내용
            - **receiverId** : 거래 대상 학생 ID
            - **createdAt** : 거래 생성일
            """)
    ResponseEntity<ApiResponse<List<BankTransactionDTO>>> bankTransactionList();

}
