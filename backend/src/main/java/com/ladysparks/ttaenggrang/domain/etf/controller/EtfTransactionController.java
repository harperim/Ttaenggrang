package com.ladysparks.ttaenggrang.domain.etf.controller;

import com.ladysparks.ttaenggrang.domain.etf.dto.EtfTransactionDTO;
import com.ladysparks.ttaenggrang.domain.etf.service.EtfTransactionService;
import com.ladysparks.ttaenggrang.domain.student.service.StudentService;
import com.ladysparks.ttaenggrang.global.docs.stock.EtfTransactionApiSpecification;
import com.ladysparks.ttaenggrang.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/etf-transactions")
public class EtfTransactionController implements EtfTransactionApiSpecification {
    private final EtfTransactionService etfTransactionService; // StockService 주입
    private final StudentService studentService;

    // 주식 매수
    @PostMapping("/buy/{etfId}")
    public ResponseEntity<ApiResponse<EtfTransactionDTO>> buyEtf(@PathVariable("etfId") Long etfId,
                                                                   @RequestParam("share_count") int shareCount,
                                                                   @RequestParam("studentId") Long studentId) {
        EtfTransactionDTO dto = etfTransactionService.buyEtf(etfId, shareCount, studentId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(dto));
    }

    // 주식 매도
    @PostMapping("/sell/{etfId}")
    public ResponseEntity<ApiResponse<EtfTransactionDTO>> sellEtf(@PathVariable("etfId") Long etfId,
                                                                      @RequestParam("share_count") int shareCount,
                                                                      @RequestParam("studentId") Long studentId) {
        EtfTransactionDTO dto = etfTransactionService.sellEtf(etfId, shareCount, studentId);

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(dto));
    }

//    // 학생 거래 내역 조회 (매수 + 매도)
//    @GetMapping
//    public ResponseEntity<ApiResponse<List<StockTransactionResponseDTO>>> getStudentTransactions() {
//        Long studentId = studentService.getCurrentStudentId();
//
//        // 학생 ID에 대한 거래 내역을 조회하고 DTO로 변환하여 반환
//        List<StockTransactionResponseDTO> transactions = stockTransactionService.getStudentTransactions(studentId);
//
//        // 거래 내역이 없을 경우 빈 리스트를 반환하면서 200 OK 응답
//        return ResponseEntity.ok(ApiResponse.success(transactions)); // 200 OK
//    }
}
