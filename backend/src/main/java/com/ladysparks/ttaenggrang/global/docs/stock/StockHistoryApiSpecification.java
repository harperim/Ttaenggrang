package com.ladysparks.ttaenggrang.global.docs.stock;

import com.ladysparks.ttaenggrang.domain.stock.dto.ChangeResponseDTO;
import com.ladysparks.ttaenggrang.domain.stock.dto.StockHistoryDTO;
import com.ladysparks.ttaenggrang.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Tag(name = "[교사/학생] 주식 추이(변동률)", description = "매수/매도에 따른 주식 변동률 관련 API")
public interface StockHistoryApiSpecification {

    @Operation(summary = "(교사/학생) 현재 주식 가격 및 변동률 조회 [조회]", description = """
            💡 **폐장(17시) 이후 계산**되어 갱신된 현재의 주식 가격 및 변동률을 조회합니다.
            
            """)
    ResponseEntity<ApiResponse<List<ChangeResponseDTO>>> getStockPrices();

    @Operation(summary = "(교사/학생) 특정 주식 가격 변동 이력 [상세 조회]", description = """
            💡 특정 주식의 가격 변동 이력 조회합니다.
            """)
    ResponseEntity<ApiResponse<List<StockHistoryDTO>>> getStockHistory(@PathVariable Long stockId);

    @Operation(summary = "(교사/학생) 모든 주식 가격 변동 이력 [전체 조회]", description = """
            💡 모든 주식의 가격 변동 이력 조회합니다.
            """)
    ResponseEntity<ApiResponse<List<StockHistoryDTO>>> getAllStockHistory();

}