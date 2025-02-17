package com.ladysparks.ttaenggrang.domain.stock.controller;

import com.ladysparks.ttaenggrang.domain.stock.dto.ChangeResponseDTO;
import com.ladysparks.ttaenggrang.domain.stock.dto.StockHistoryDTO;
import com.ladysparks.ttaenggrang.domain.stock.service.StockHistoryService;
import com.ladysparks.ttaenggrang.domain.teacher.service.TeacherService;
import com.ladysparks.ttaenggrang.global.docs.stock.StockHistoryApiSpecification;
import com.ladysparks.ttaenggrang.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/stock-history")
public class StockHistoryController implements StockHistoryApiSpecification {

    private final StockHistoryService stockHistoryService;
    private final TeacherService teacherService;

    // 현재 주식 가격 및 변동률 계산
//    @GetMapping("/current")
//    public ResponseEntity<ApiResponse<List<ChangeResponseDTO>>> getStockPrices() {
//        List<ChangeResponseDTO> stockPrices = stockHistoryService.updateStockPricesForMarketOpening();
//        return ResponseEntity.ok(ApiResponse.success(stockPrices));
//    }

    // 특정 주식의 가격 변동 이력 조회
//    @GetMapping("/stocks/{stockId}")
//    public ResponseEntity<ApiResponse<List<StockHistoryDTO>>> getStockHistory(@PathVariable Long stockId) {
//        List<StockHistoryDTO> historyList = stockHistoryService.getStockHistoryByStockId(stockId);
//        return ResponseEntity.ok(ApiResponse.success(historyList));
//    }

    // 모든 주식 가격 변동 이력 조회
    @GetMapping("/stocks")
    public ResponseEntity<ApiResponse<Map<Long, List<StockHistoryDTO>>>> getLast5DaysStockHistory() {
        Long teacherId = teacherService.getCurrentTeacherId();
        Map<Long, List<StockHistoryDTO>> historyMap = stockHistoryService.getLast5WeekdaysStockHistory(teacherId);
        return ResponseEntity.ok(ApiResponse.success(historyMap));
    }

}