package com.ladysparks.ttaenggrang.global.docs.stock;

import com.ladysparks.ttaenggrang.domain.stock.dto.StockDTO;
import com.ladysparks.ttaenggrang.domain.stock.dto.StockSummaryDTO;
import com.ladysparks.ttaenggrang.domain.stock.dto.StudentStockDTO;
import com.ladysparks.ttaenggrang.domain.teacher.dto.StudentStockTransactionDTO;
import com.ladysparks.ttaenggrang.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Tag(name = "[교사/학생] 주식 상품", description = "주식 상품 관련 API")
public interface StockApiSpecification {

    @Operation(summary = "(교사/학생) 주식 상품 요약 [전체 조회]", description = """
            💡 전체 주식 상품 목록을 조회합니다.

            ---

            **[ 요청 값 ]**
            - 없음

            **[ 응답 필드 ]**
            - **id** : 주식 ID
            - **createdDate**: 등록일
            - **name** : 종목명
            - **type** : 주식 종류 (일반 주식/ETF)
            - **category** : 카테고리
            - **pricePerShare** : 현재 가격 (한 주당 가격)
            - **priceChangeRate** : 주식 가격 변동률 (%)
            - **transactionFrequency** : 거래 활성도 (%)

            ---

            **[ 설명 ]**
            - 전체 주식 상품을 목록 형태로 조회합니다.
            - 가격 변동률은 (오늘 가격 - 어제 가격) / 어제 가격 * 100 으로 계산됩니다.
            - 거래 활성도는 최근 7일간 거래량을 기반으로 계산됩니다.
            """)
    ResponseEntity<ApiResponse<List<StockSummaryDTO>>> getStockSummaryList();

    @Operation(summary = "(교사/학생) 주식 상품 [전체 조회]", description = """
            💡 전체 주식 상품 목록을 조회합니다.
            """)
    ResponseEntity<ApiResponse<List<StockDTO>>> getStockList();

//    @Operation(summary = "(교사/학생) 주식 상품 [상세 조회]", description = """
//            💡 특정 주식 상품을 상세 조회합니다.
//            """)
//    ResponseEntity<ApiResponse<StockDTO>> getStock(@PathVariable("stockId") Long stockId);

    @Operation(summary = "(교사) 주식 상품 [등록]", description = """
            💡 교사가 판매할 주식 상품을 등록합니다.
            """)
    ResponseEntity<ApiResponse<StockDTO>> addStock(@RequestBody StockDTO stockDto);

    @Operation(summary = "(학생) 보유 주식 [전체 조회]", description = """
            💡 학생이 보유한 주식 목록을 조회합니다.
            """)
    ResponseEntity<ApiResponse<List<StudentStockTransactionDTO>>> getStudentStocks();

}