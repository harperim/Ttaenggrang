package com.ladysparks.ttaenggrang.domain.etf.controller;

import com.ladysparks.ttaenggrang.domain.etf.dto.EtfTransactionDTO;
import com.ladysparks.ttaenggrang.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/etf-market")
public class EtfMarketController {
    //ETF 매수
//    @PostMapping("/{etfId}/buy")
//    public ResponseEntity<ApiResponse<EtfTransactionDTO>> buyEtf(@PathVariable("etfId") Long etfId,
//                                                                 @RequestParam("share_count") int shareCount,
//                                                                 @RequestParam("studentId") Long studentId) {
//
//        // 주식 매수 서비스 호출
//        EtfTransactionDTO dto = etfService.buyEtf(etfId, shareCount, studentId);
//
//        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(dto));
//    }
//
//    //ETF 매도
//    @PostMapping("/{etfId}/sell")
//    public ResponseEntity<ApiResponse<EtfTransactionDTO>> sellEtf(@PathVariable("etfId") Long etfId,
//                                                                  @RequestParam("share_count") int shareCount,
//                                                                  @RequestParam("studentId") Long studentId) {
//
//        // 주식 매수 서비스 호출
//        EtfTransactionDTO dto = etfService.sellEtf(etfId, shareCount, studentId);
//
//        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(dto));
//    }
}
