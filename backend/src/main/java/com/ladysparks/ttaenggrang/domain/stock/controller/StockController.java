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
    @PostMapping
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

    // 학생 보유 주식 조회
    @GetMapping("/students/{studentId}")
    public ResponseEntity<List<StudentStockDTO>> getStudentStocks(@PathVariable Long studentId) {
        List<StudentStockDTO> stockList = stockService.getStudentStocks(studentId);
        return ResponseEntity.ok(stockList);
    }

}