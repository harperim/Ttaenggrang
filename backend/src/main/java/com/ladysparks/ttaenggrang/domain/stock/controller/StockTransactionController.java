package com.ladysparks.ttaenggrang.domain.stock.controller;

import com.ladysparks.ttaenggrang.domain.stock.dto.StockTransactionDTO;
import com.ladysparks.ttaenggrang.domain.stock.dto.StockTransactionResponseDTO;
import com.ladysparks.ttaenggrang.domain.stock.service.StockTransactionService;
import com.ladysparks.ttaenggrang.domain.student.service.StudentService;
import com.ladysparks.ttaenggrang.global.docs.stock.StockTransactionApiSpecification;
import com.ladysparks.ttaenggrang.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/stock-transactions")
public class StockTransactionController implements StockTransactionApiSpecification {

    private final StockTransactionService stockTransactionService; // StockService 주입
    private final StudentService studentService;

    // 주식 매수
    @PostMapping("/buy/{stockId}")
    public ResponseEntity<ApiResponse<StockTransactionDTO>> buyStock(@PathVariable("stockId") Long stockId,
                                                                     @RequestParam("share_count") int shareCount,
                                                                     @RequestParam("studentId") Long studentId) {
        StockTransactionDTO dto = stockTransactionService.buyStock(stockId, shareCount, studentId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(dto));
    }

    // 주식 매도
    @PostMapping("/sell/{stockId}")
    public ResponseEntity<ApiResponse<StockTransactionDTO>> sellStock(@PathVariable("stockId") Long stockId,
                                                                      @RequestParam("share_count") int shareCount,
                                                                      @RequestParam("studentId") Long studentId) {
        StockTransactionDTO dto = stockTransactionService.sellStock(stockId, shareCount, studentId);

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(dto));
    }

    // 학생 거래 내역 조회 (매수 + 매도)
    @GetMapping
    public ResponseEntity<ApiResponse<List<StockTransactionResponseDTO>>> getStudentTransactions() {
        Long studentId = studentService.getCurrentStudentId();

        // 학생 ID에 대한 거래 내역을 조회하고 DTO로 변환하여 반환
        List<StockTransactionResponseDTO> transactions = stockTransactionService.getStudentTransactions(studentId);

        // 거래 내역이 없을 경우 빈 리스트를 반환하면서 200 OK 응답
        return ResponseEntity.ok(ApiResponse.success(transactions)); // 200 OK
    }

}