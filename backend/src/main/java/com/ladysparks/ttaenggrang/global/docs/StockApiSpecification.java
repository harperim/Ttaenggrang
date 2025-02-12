package com.ladysparks.ttaenggrang.global.docs;

import com.ladysparks.ttaenggrang.domain.stock.dto.OpenResponseDTO;
import com.ladysparks.ttaenggrang.domain.stock.dto.StockDTO;
import com.ladysparks.ttaenggrang.domain.stock.dto.StockTransactionDTO;
import com.ladysparks.ttaenggrang.domain.stock.dto.StudentStockDTO;
import com.ladysparks.ttaenggrang.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@Tag(name = "Stock", description = "주식 API")
public interface StockApiSpecification {

    @Operation(summary = "주식 전체 조회", description = "💡 전체 주식을 조회합니다.")
    @GetMapping
    public ResponseEntity<List<StockDTO>> getStocks();

    @Operation(summary = "주식 상세 조회", description = "💡 주식 ID로 주식을 조회합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<StockDTO>> getStock(@PathVariable("stockId") Long stockId);

    @Operation(summary = "주식 등록", description = "주식을 등록 합니다")
    @PostMapping
    public ResponseEntity<ApiResponse<StockDTO>> addStock(@RequestBody StockDTO stockDto);


    @Operation(summary = "주식 매수", description = "💡 주식 ID와 수량으로 주식을 매수합니다.")
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



//    @Operation(summary = "주식장 활성화/비활성화", description = "💡 주식장 활성화/비활성화")
//    @PostMapping("/manage")
//    public ResponseEntity<Map<String, Boolean>> manageStockMarket(@RequestParam boolean openMarket);
//
//    @Operation(summary = "주식장 활성화/비활성화 조회", description = "💡 주식장 활성화/비활성화 조회")
//    @GetMapping("/status")
//    public ResponseEntity<Map<String, Boolean>> getMarketStatus();
//
//    @Operation(summary = "주식 개장시간, 폐장 시간 변경", description = "💡주식 개장시간, 폐장 시간 변경")
//    public ResponseEntity<ApiResponse<StockDTO>> updateMarketTimeForAllStocks(@RequestBody StockDTO stockDTO);
//
    @Operation(summary = "주식시장 활성화 여부 조회", description = "💡 주식시장 활성화 여부 조회")
    @GetMapping("/isMarketActive")
    public boolean isMarketActive();
    @Operation(summary = "주식시장 활성화/비활성화 설정", description = "💡 주식시장 활성화/비활성화 설정 (선생님만 가능)")
    @PostMapping("/setMarketActive")
    public void setMarketActive(@RequestParam boolean isActive);
    @Operation(summary = " 현재 주식 거래 가능 여부 조회", description = "💡  현재 주식 거래 가능 여부 조회 (시장 활성화 + 9~17시)")
    @GetMapping("/isTradingAllowed")
    public boolean isTradingAllowed();



}