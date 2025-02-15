package com.ladysparks.ttaenggrang.global.docs.stock;

import com.ladysparks.ttaenggrang.domain.stock.dto.StockTransactionDTO;
import com.ladysparks.ttaenggrang.domain.stock.dto.StudentStockDTO;
import com.ladysparks.ttaenggrang.domain.stock.dto.StockTransactionResponseDTO;
import com.ladysparks.ttaenggrang.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Tag(name = "[학생] 주식 거래", description = "주식 거래 관련 API")
public interface StockTransactionApiSpecification {

    @Operation(summary = "(학생) 주식 매수 [요청]", description = """
        💡 특정 학생이 주식을 매수하는 요청을 처리합니다.
        
        ---
        
        **[ 요청 값 ]**
        - **studentId** : 매수할 학생 ID
        - **stockId** : 매수할 주식 ID
        - **shareQuantity** : 매수할 주식 수량
        
        **[ 응답 필드 ]**
        - **id** : 주식 거래 ID
        - **studentId** : 거래한 학생 ID
        - **stockId** : 거래한 주식 ID
        - **shareQuantity** : 거래한 주식 수량
        - **purchasePricePerShare** : 거래 당시 1주 가격
        - **totalPrice** : 총 거래 금액
        - **returnRate** : 손익률 (매도 시 적용)
        - **transactionType** : 거래 유형
            - 매수 → **BUY**
        - **totalQuantity** : 학생이 보유한 총 주식 수량
        - **transactionDate** : 거래 날짜
        
        ---
        
        **[ 설명 ]**
        - 특정 `studentId`가 `stockId`에 대한 주식을 `shareQuantity`만큼 매수합니다.
        - 거래 유형은 `매수(BUY)`만 포함됩니다.
        """)
    ResponseEntity<ApiResponse<StockTransactionDTO>> buyStock(@PathVariable("stockId") Long stockId,
                                                                     @RequestParam("share_count") int shareCount,
                                                                     @RequestParam("studentId") Long studentId);

    @Operation(summary = "(학생) 주식 매도 [요청]", description = """
        💡 특정 학생이 주식을 매도하는 요청을 처리합니다.
        
        ---
        
        **[ 요청 값 ]**
        - **studentId** : 매도할 학생 ID
        - **stockId** : 매도할 주식 ID
        - **shareQuantity** : 매도할 주식 수량
        
        **[ 응답 필드 ]**
        - **id** : 주식 거래 ID
        - **studentId** : 거래한 학생 ID
        - **stockId** : 거래한 주식 ID
        - **shareQuantity** : 거래한 주식 수량
        - **purchasePricePerShare** : 거래 당시 1주 가격
        - **totalPrice** : 총 거래 금액
        - **returnRate** : 손익률 (매도 시 적용)
        - **transactionType** : 거래 유형
            - 매도 → **SELL**
        - **totalQuantity** : 학생이 보유한 총 주식 수량
        - **transactionDate** : 거래 날짜
        
        ---
        
        **[ 설명 ]**
        - 특정 `studentId`가 `stockId`에 대한 주식을 `shareQuantity`만큼 매도합니다.
        - 거래 유형은 `매도(SELL)`만 포함됩니다.
        """)
    ResponseEntity<ApiResponse<StockTransactionDTO>> sellStock(@PathVariable("stockId") Long stockId,
                                                                     @RequestParam("share_count") int shareCount,
                                                                     @RequestParam("studentId") Long studentId);

    @Operation(summary = "(학생) 주식 거래 내역 [조회]", description = """
        💡 특정 학생의 주식 거래(매수/매도) 내역을 조회합니다.
        
        ---
        
        **[ 요청 값 ]**
        - **studentId** : 조회할 학생 ID
        - **stockId** : 조회할 주식 ID
        
        **[ 응답 필드 ]**
        - **studentId** : 학생 ID
        - **stockId** : 거래된 주식 ID
        - **name** : 주식명
        - **type** : 주식 타입
        - **transactionType** : 거래 타입
            - 매수 → **BUY**
            - 매도 → **SELL**
        - **shareCount** : 주식 거래 수량
        - **transactionDate** : 거래 날짜
        - **purchasePricePerShare** : 거래 당시 1주 가격
        
        ---
        
        **[ 설명 ]**
        - 특정 `studentId`의 주식 거래 내역을 조회합니다.
        - 거래 유형에는 `매수(BUY)`와 `매도(SELL)`이 포함됩니다.
        """)
    ResponseEntity<ApiResponse<List<StockTransactionResponseDTO>>> getStudentTransactions();

}