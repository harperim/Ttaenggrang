package com.ladysparks.ttaenggrang.domain.stock.controller;

import com.ladysparks.ttaenggrang.domain.stock.dto.ChangeResponseDTO;
import com.ladysparks.ttaenggrang.domain.stock.dto.StockHistoryDTO;
import com.ladysparks.ttaenggrang.domain.stock.service.StockHistoryService;
import com.ladysparks.ttaenggrang.global.docs.stock.StockHistoryApiSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/stock-history")
public class StockHistoryController implements StockHistoryApiSpecification {

    private final StockHistoryService stockHistoryService;

    // 주식 가격 및 변동률 조회
    @GetMapping("/prices")
    public ResponseEntity<List<ChangeResponseDTO>> getStockPrices() {
        List<ChangeResponseDTO> stockPrices = stockHistoryService.updateStockPricesForMarketOpening();
        return ResponseEntity.ok(stockPrices);
    }

    // 특정 주식의 가격 변동 이력 조회
    @GetMapping("/history/{stockId}")
    public ResponseEntity<List<StockHistoryDTO>> getStockHistory(@PathVariable Long stockId) {
        List<StockHistoryDTO> historyList = stockHistoryService.getStockHistoryByStockId(stockId);
        return ResponseEntity.ok(historyList);
    }

    // 모든 주식 가격 변동 이력 조회
    @GetMapping
    public ResponseEntity<List<StockHistoryDTO>> getAllStockHistory() {
        List<StockHistoryDTO> historyList = stockHistoryService.getAllStockHistory();
        return ResponseEntity.ok(historyList);
    }

}