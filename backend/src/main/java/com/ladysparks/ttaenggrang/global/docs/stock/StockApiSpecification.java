package com.ladysparks.ttaenggrang.global.docs.stock;

import com.ladysparks.ttaenggrang.domain.stock.dto.*;
import com.ladysparks.ttaenggrang.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "[교사/학생] 주식", description = "주식 API")
public interface StockApiSpecification {

    @Operation(summary = "(교사/학생) 주식 전체 조회", description = "💡 전체 주식을 조회합니다.")
    @GetMapping
    public ResponseEntity<List<StockDTO>> getStocks();

    @Operation(summary = "(교사/학생) 주식 상세 조회", description = "💡 주식 ID로 주식을 조회합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<StockDTO>> getStock(@PathVariable("stockId") Long stockId);

    @Operation(summary = "(교사) 주식 등록", description = "주식을 등록 합니다")
    @PostMapping
    public ResponseEntity<ApiResponse<StockDTO>> addStock(@RequestBody StockDTO stockDto);


    @Operation(summary = "(학생) 주식 매수", description = "💡 주식 ID와 수량으로 주식을 매수합니다.")
    @PostMapping("/{stockId}/buy")
    public ResponseEntity<ApiResponse<StockTransactionDTO>> buyStock(@PathVariable("stockId") Long stockId,
                                                                     @RequestParam("share_count") int shareCount,
                                                                     @RequestParam("studentId") Long studentId);

    @Operation(summary = "주식 매도", description = "💡 주식 ID와 수량으로 주식을 매도합니다.")
    @PostMapping("/{stockId}/sell")
    public ResponseEntity<ApiResponse<StockTransactionDTO>> sellStock(@PathVariable("stockId") Long stockId,
                                                                     @RequestParam("share_count") int shareCount,
                                                                     @RequestParam("studentId") Long studentId);

    @Operation(summary = "학생 보유 주식 조회", description = "학생 보유 주식 조회")
    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<StudentStockDTO>> getStudentStocks(@PathVariable Long studentId);



    @Operation(summary = "주식시장 활성화 여부 조회", description = "💡 주식시장 활성화 여부 조회")
    @GetMapping("/isManualOverride")
    public ResponseEntity<Boolean> getMarketStatus();

    @Operation(summary = "주식시장 활성화/비활성화 설정", description = "💡 주식시장 활성화/비활성화 설정 (선생님만 가능)")
    @PostMapping("/status")
    public ResponseEntity<String> setMarketStatus(@RequestParam @Parameter(description = "주식 시장 활성화 여부") boolean isActive);


    @Operation(summary = " 현재 주식 거래 가능 여부 조회", description = "💡  현재 주식 거래 가능 여부 조회 (시장 활성화 + 9~17시)")
    @GetMapping("/isTradingAllowed")
    public ResponseEntity<Boolean> isTradingAllowed();


    // 주식 가격 및 변동률 조회
    @Operation(summary = " 주식 가격 및 변동률 조회", description = "💡  주식 가격 및 변동률 조회 합니다.")
    @GetMapping("/prices")
    public ResponseEntity<List<ChangeResponseDTO>> getStockPrices();



}