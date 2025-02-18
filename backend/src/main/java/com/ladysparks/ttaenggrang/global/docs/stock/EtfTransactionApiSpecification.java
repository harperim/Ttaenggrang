package com.ladysparks.ttaenggrang.global.docs.stock;

import com.ladysparks.ttaenggrang.domain.etf.dto.EtfTransactionDTO;
import com.ladysparks.ttaenggrang.domain.etf.dto.EtfTransactionResponseDTO;
import com.ladysparks.ttaenggrang.domain.stock.dto.StockTransactionResponseDTO;
import com.ladysparks.ttaenggrang.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Tag(name = "[학생] ETF 거래", description = "ETF 거래 관련 API")
public interface EtfTransactionApiSpecification {

    @Operation(summary = "(학생) ETF 매수 [요청]", description = """
        💡 특정 학생이 ETF을 매수하는 요청을 처리합니다.
        
        ---
        
        **[ 요청 값 ]**
        - **studentId** : 매수할 학생 ID
        - **etfId** : 매수할 ETF ID
        - **shareQuantity** : 매수할 ETF 수량
        
        **[ 응답 필드 ]**
        - **id** : ETF 거래 ID
        - **studentId** : 거래한 학생 ID
        - **etfId** : 거래한 ETF ID
        - **shareQuantity** : 거래한 ETF 수량
        - **purchasePricePerShare** : 거래 당시 1주 가격
        - **totalPrice** : 총 거래 금액
        - **returnRate** : 손익률 (매도 시 적용)
        - **transactionType** : 거래 유형
            - 매수 → **BUY**
        - **totalQuantity** : 학생이 보유한 총 ETF 수량
        - **transactionDate** : 거래 날짜
        
        ---
        
        **[ 설명 ]**
        - 특정 `studentId`가 `etfId`에 대한 ETF을 `shareQuantity`만큼 매수합니다.
        - 거래 유형은 `매수(BUY)`만 포함됩니다.
        """)
    @PostMapping("/buy/{etfId}")
    public ResponseEntity<ApiResponse<EtfTransactionDTO>> buyEtf(@PathVariable("etfId") Long etfId,
                                                                 @RequestParam("share_count") int shareCount,
                                                                 @RequestParam("studentId") Long studentId);

    @Operation(summary = "(학생) ETF 매도 [요청]", description = """
        💡 특정 학생이 ETF을 매도하는 요청을 처리합니다.
        
        ---
        
        **[ 요청 값 ]**
        - **studentId** : 매도할 학생 ID
        - **etfId** : 매도할 ETF ID
        - **shareQuantity** : 매도할 ETF 수량
        
        **[ 응답 필드 ]**
        - **id** : ETF 거래 ID
        - **studentId** : 거래한 학생 ID
        - **etfId** : 거래한 ETF ID
        - **shareQuantity** : 거래한 ETF 수량
        - **purchasePricePerShare** : 거래 당시 1주 가격
        - **totalPrice** : 총 거래 금액
        - **returnRate** : 손익률 (매도 시 적용)
        - **transactionType** : 거래 유형
            - 매도 → **SELL**
        - **totalQuantity** : 학생이 보유한 총 ETF 수량
        - **transactionDate** : 거래 날짜
        
        ---
        
        **[ 설명 ]**
        - 특정 `studentId`가 `etfId`에 대한 ETF을 `shareQuantity`만큼 매도합니다.
        - 거래 유형은 `매도(SELL)`만 포함됩니다.
        """)
    @PostMapping("/sell/{etfId}")
    public ResponseEntity<ApiResponse<EtfTransactionDTO>> sellEtf(@PathVariable("etfId") Long etfId,
                                                                  @RequestParam("share_count") int shareCount,
                                                                  @RequestParam("studentId") Long studentId);

    @Operation(summary = "(학생) ETF 거래 내역 [조회]", description = """
        💡 특정 학생의 ETF 거래(매수/매도) 내역을 조회합니다.
        
        ---
        
        **[ 요청 값 ]**
        - **studentId** : 조회할 학생 ID
        - **etfId** : 조회할 ETF ID
        
        **[ 응답 필드 ]**
        - **studentId** : 학생 ID
        - **etfId** : 거래된 ETF ID
        - **name** : ETF명
        - **type** : ETF 타입
        - **transactionType** : 거래 타입
            - 매수 → **BUY**
            - 매도 → **SELL**
        - **shareCount** : ETF 거래 수량
        - **currentPrice** : 현재 ETF 가격
        - **transactionDate** : 거래 날짜
        - **purchasePricePerShare** : 거래 당시 1주 가격
        
        ---
        
        **[ 설명 ]**
        - 특정 `studentId`의 ETF 거래 내역을 조회합니다.
        - 거래 유형에는 `매수(BUY)`와 `매도(SELL)`이 포함됩니다.
        """)
    @GetMapping
    public ResponseEntity<ApiResponse<List<EtfTransactionResponseDTO>>> getStudentTransactions();
}
