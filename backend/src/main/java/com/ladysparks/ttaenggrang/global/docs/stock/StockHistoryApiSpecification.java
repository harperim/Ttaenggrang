package com.ladysparks.ttaenggrang.global.docs.stock;

import com.ladysparks.ttaenggrang.domain.stock.dto.ChangeResponseDTO;
import com.ladysparks.ttaenggrang.domain.stock.dto.StockHistoryDTO;
import com.ladysparks.ttaenggrang.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Map;

@Tag(name = "[교사/학생] 주식 추이(변동률)", description = "매수/매도에 따른 주가 변동률 관련 API")
public interface StockHistoryApiSpecification {

//    @Operation(summary = "(교사/학생) 현재 주식 가격 및 변동률 조회 [조회]", description = """
//            💡 **폐장(17시) 이후 계산**되어 갱신된 현재의 주식 가격 및 변동률을 조회합니다.
//
//            """)
//    ResponseEntity<ApiResponse<List<ChangeResponseDTO>>> getStockPrices();

//    @Operation(summary = "(교사/학생) 특정 주식 가격 변동 이력 [상세 조회]", description = """
//            💡 특정 주식의 가격 변동 이력 조회합니다.
//            """)
//    ResponseEntity<ApiResponse<List<StockHistoryDTO>>> getStockHistory(@PathVariable Long stockId);


    /**
     * 📌 (교사) 각 주식의 최근 5개 평일 변동 이력 조회 (오늘 포함, 주말 제외, 오래된 순서)
     */
    @Operation(summary = "(교사/학생) 최근 5개 평일 주식 변동 이력 조회", description = """
        💡 학급 내 각 주식의 최근 5개 평일(월~금) 변동 이력을 조회합니다.
        
        ---
        
        **[ 응답 필드 ]**
        
        - **stockId** : 주식 ID
        - **historyList** : 최근 5개 평일 변동 이력 리스트
        
        **[ historyList 내 개별 항목 필드 ]**
        
        - **id** : 변동 이력 ID
        - **price** : 해당 날짜의 주가
        - **priceChangeRate** : 가격 변동률 (%)
        - **date** : 변동 날짜
        
        ---

        **[ 설명 ]**
        - 주말(토요일, 일요일) 데이터를 제외하고 최근 5개의 평일(월~금) 데이터를 반환합니다.
        - 오늘이 평일이면 오늘을 포함하여 5개, 오늘이 주말이면 가장 최근 평일부터 5개 반환합니다.
        """)
    ResponseEntity<ApiResponse<Map<Long, List<StockHistoryDTO>>>> getLast5DaysStockHistory();

}