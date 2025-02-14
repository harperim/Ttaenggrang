package com.ladysparks.ttaenggrang.global.docs.stock;

import com.ladysparks.ttaenggrang.domain.stock.dto.*;
import com.ladysparks.ttaenggrang.domain.stock.entity.StockHistory;
import com.ladysparks.ttaenggrang.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "[교사/학생] 주식 상품", description = "주식 상품 관련 API")
public interface StockApiSpecification {

    @Operation(summary = "(교사/학생) 주식 상품 [전체 조회]", description = """
            💡 전체 주식 상품 목록을 조회합니다.
            """)
    ResponseEntity<List<StockDTO>> getStocks();

    @Operation(summary = "(교사/학생) 주식 상품 [상세 조회]", description = """
            💡 주식 상품을 상세 조회합니다.
            """)
    ResponseEntity<ApiResponse<StockDTO>> getStock(@PathVariable("stockId") Long stockId);

    @Operation(summary = "(교사) 주식 상품 등록", description = """
            💡 주식 상품을 등록 합니다
            """)
    ResponseEntity<ApiResponse<StockDTO>> addStock(@RequestBody StockDTO stockDto);

    @Operation(summary = "(학생) 보유 주식 조회", description = "학생 보유 주식 조회")
    ResponseEntity<List<StudentStockDTO>> getStudentStocks(@PathVariable Long studentId);

}