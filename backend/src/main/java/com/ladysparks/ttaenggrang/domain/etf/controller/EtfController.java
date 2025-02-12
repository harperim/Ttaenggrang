package com.ladysparks.ttaenggrang.domain.etf.controller;

import com.ladysparks.ttaenggrang.domain.etf.dto.EtfDTO;
import com.ladysparks.ttaenggrang.domain.etf.dto.EtfTransactionDTO;
import com.ladysparks.ttaenggrang.domain.etf.service.EtfService;
import com.ladysparks.ttaenggrang.domain.stock.dto.StockDTO;
import com.ladysparks.ttaenggrang.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/etfs")
public class EtfController  {
    private final EtfService etfService; // StockService 주입

    //ETF 생성
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<EtfDTO>> addEtf(@RequestParam Long studentId,
                                                      @RequestParam List<Long> stockIds) {
        // ETF 생성 서비스 호출
        EtfDTO createdEtfDTO = etfService.createETF(studentId, stockIds);

        // ETF 생성 후 성공적인 응답 반환
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(createdEtfDTO));
    }

    // 주식 목록 전체 조회
    @GetMapping
    public ResponseEntity<List<EtfDTO>> getEtfs() {
        List<EtfDTO> result = etfService.findEtfs(); // 모든 주식 정보를 반환
        return ResponseEntity.ok(result); // HTTP 200 OK와 함께 결과 반환
    }

    // 주식 상세 조회
    @GetMapping("/{etfId}")
    public ResponseEntity<EtfDTO> getEtf(@PathVariable("etfId") int etfId) {
        Optional<EtfDTO> result = etfService.findEtf(etfId);

        // 값이 없으면 404 Not Found 반환
        if (result.isPresent()) {
            return ResponseEntity.ok(result.get()); // 값이 있으면 200 OK와 함께 결과 반환
        } else {
            return ResponseEntity.notFound().build(); // 값이 없으면 404 Not Found 반환
        }
    }
    //ETF 매수
    @PostMapping("/{etfId}/buy")
    public ResponseEntity<ApiResponse<EtfTransactionDTO>> buyEtf(@PathVariable("etfId") int etfId,
                                                                     @RequestParam("share_count") int shareCount,
                                                                     @RequestParam("studentId") Long studentId) {

        // 주식 매수 서비스 호출
        EtfTransactionDTO dto = etfService.buyEtf(etfId, shareCount, studentId);

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(dto));
    }

    //ETF 매도
    @PostMapping("/{etfId}/sell")
    public ResponseEntity<ApiResponse<EtfTransactionDTO>> sellEtf(@PathVariable("etfId") int etfId,
                                                                      @RequestParam("share_count") int shareCount,
                                                                      @RequestParam("studentId") Long studentId) {

        // 주식 매수 서비스 호출
        EtfTransactionDTO dto = etfService.sellEtf(etfId, shareCount, studentId);

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(dto));
    }

//    // 가격 변동 (관리자가 호출)
//    @PostMapping("/{etfId}/update-price")
//    public ResponseEntity<ApiResponse<EtfDTO>> updateEtfPrice(
//            @PathVariable("etfId") int etfId) {
//
//        // 주식 가격 업데이트 서비스 호출
//        EtfDTO updatedEtf = etfService.updateEtfPrice(etfId);
//        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(updatedEtf));
//    }
}
