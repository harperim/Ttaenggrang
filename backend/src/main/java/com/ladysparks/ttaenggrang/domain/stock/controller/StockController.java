package com.ladysparks.ttaenggrang.domain.stock.controller;

import com.ladysparks.ttaenggrang.domain.stock.dto.*;
import com.ladysparks.ttaenggrang.domain.stock.entity.StockHistory;
import com.ladysparks.ttaenggrang.global.docs.stock.StockApiSpecification;
import com.ladysparks.ttaenggrang.domain.stock.service.StockService;
import com.ladysparks.ttaenggrang.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/stocks")
public class StockController implements StockApiSpecification {
    private final StockService stockService; // StockService 주입

    //주식 등록

    @PostMapping("/add")
    public ResponseEntity<ApiResponse<StockDTO>> addStock(@RequestBody StockDTO stockDTO) {
        // 주식 등록 서비스 호출
        StockDTO createdStockDTO = stockService.registerStock(stockDTO);

        // 주식 등록 후 성공적인 응답 반환
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(createdStockDTO));
    }



    // 주식 목록 전체 조회
    @GetMapping
    public ResponseEntity<List<StockDTO>> getStocks() {
        List<StockDTO> result = stockService.findStocks(); // 모든 주식 정보를 반환
        return ResponseEntity.ok(result); // HTTP 200 OK와 함께 결과 반환
    }

    // 주식 상세 조회
    @GetMapping("/{stockId}")
    public ResponseEntity<ApiResponse<StockDTO>> getStock(@PathVariable("stockId") Long stockId) {
        Optional<StockDTO> result = stockService.findStock(stockId);

        // 값이 없으면 404 Not Found 반환
        if (result.isPresent()) {
            return ResponseEntity.ok(ApiResponse.success(result.get())); // 값이 있으면 200 OK와 함께 결과 반환
        } else {
            return ResponseEntity.notFound().build(); // 값이 없으면 404 Not Found 반환
        }
    }

    //주식 매수
    @PostMapping("/{stockId}/buy")
    public ResponseEntity<ApiResponse<StockTransactionDTO>> buyStock(@PathVariable("stockId") Long stockId,
                                                                     @RequestParam("share_count") int shareCount,
                                                                     @RequestParam("studentId") Long studentId) {

        // 주식 매수 서비스 호출
        StockTransactionDTO dto = stockService.buyStock(stockId, shareCount, studentId);

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(dto));
    }

    // 주식 매도
    @PostMapping("/{stockId}/sell")
    public ResponseEntity<ApiResponse<StockTransactionDTO>> sellStock(@PathVariable("stockId") Long stockId,
                                                                     @RequestParam("share_count") int shareCount,
                                                                     @RequestParam("studentId") Long studentId) {

        // 주식 매수 서비스 호출
        StockTransactionDTO dto = stockService.sellStock(stockId, shareCount, studentId);

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(dto));
    }

    // 학생 거래 내역
    @GetMapping("/students/{studentId}/transactions")
    public ResponseEntity<List<TransactionResponseDTO>> getStudentTransactions(@PathVariable Long studentId) {
        // 학생 ID에 대한 거래 내역을 조회하고 DTO로 변환하여 반환
        List<TransactionResponseDTO> transactions = stockService.getStudentTransactions(studentId);

        // 거래 내역이 없을 경우 빈 리스트를 반환하면서 200 OK 응답
        return ResponseEntity.ok(transactions); // 200 OK
    }


    //학생 보유 주식 조회
    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<StudentStockDTO>> getStudentStocks(@PathVariable Long studentId) {
        List<StudentStockDTO> stockList = stockService.getStudentStocks(studentId);
        return ResponseEntity.ok(stockList);
    }



    // 주식시장 활성화 여부 조회 (선생님이 설정한 값)
    @GetMapping("/isManualOverride")
    public ResponseEntity<Boolean> getMarketStatus() {
        boolean isMarketActive = stockService.isMarketActive();
        return ResponseEntity.ok(isMarketActive);
    }


    // 주식시장 활성화/비활성화 설정 (선생님이 버튼으로 설정)
    @PostMapping("/status")
    public ResponseEntity<String> setMarketStatus(@RequestParam @Parameter(description = "주식 시장 활성화 여부") boolean isActive) {
        stockService.setMarketStatus(isActive);
        return ResponseEntity.ok("주식 및 ETF 시장 상태가 변경되었습니다.");
    }


    // 현재 주식 거래 가능 여부 조회 (시장 활성화 + 시간 체크)
    @GetMapping("/trading-allowed")
    public ResponseEntity<Boolean> isTradingAllowed() {
        boolean isAllowed = stockService.isTradingAllowed();
        return ResponseEntity.ok(isAllowed);
    }


    // 주식 가격 및 변동률 조회
    @GetMapping("/prices")
    public ResponseEntity<List<ChangeResponseDTO>> getStockPrices() {
        List<ChangeResponseDTO> stockPrices = stockService.updateStockPricesForMarketOpening();
        return ResponseEntity.ok(stockPrices);
    }


    // 특정 주식의 가격 변동 이력 조회
    @GetMapping("/history/{stockId}")
    public ResponseEntity<List<StockHistoryDTO>> getStockHistory(@PathVariable Long stockId) {
        List<StockHistoryDTO> historyList = stockService.getStockHistoryByStockId(stockId);
        return ResponseEntity.ok(historyList);
    }


    // 모든 주식 가격 변동 이력 조회
    @GetMapping("/all/history")
    public ResponseEntity<List<StockHistoryDTO>> getAllStockHistory() {
        List<StockHistoryDTO> historyList = stockService.getAllStockHistory();
        return ResponseEntity.ok(historyList);
    }

}







