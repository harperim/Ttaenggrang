package com.ladysparks.ttaenggrang.global.docs.stock;

import com.ladysparks.ttaenggrang.domain.etf.dto.EtfDTO;
import com.ladysparks.ttaenggrang.domain.etf.dto.EtfTransactionDTO;
import com.ladysparks.ttaenggrang.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
@Tag(name = "[교사/학생] Etf", description = "ETF API")
public interface EtfApiSpecification {
    @Operation(summary = "(교사/학생) ETF 전체 조회", description = "💡 전체 ETF을 조회합니다.")
    @GetMapping
    public ResponseEntity<List<EtfDTO>> getEtfs();

    @Operation(summary = "(교사/학생) ETF 상세 조회", description = "💡 ETF ID로 주식을 조회합니다.")
    @GetMapping
    public ResponseEntity<EtfDTO> getEtf(@PathVariable("etfId") Long etfId);

    @Operation(summary = "(학생) ETF 매수", description = "💡 ETF ID와 수량으로 주식을 매수합니다.")
    @PostMapping("/{etfId}/buy")
    public ResponseEntity<ApiResponse<EtfTransactionDTO>> buyEtf(@PathVariable("etfId") Long etfId,
                                                                   @RequestParam("share_count") int shareCount,
                                                                   @RequestParam("studentId") Long studentId);

    @Operation(summary = "(학생) ETF 매도", description = "💡 ETF ID와 수량으로 주식을 매도합니다.")
    @PostMapping("/{etfId}/sell")
    public ResponseEntity<ApiResponse<EtfTransactionDTO>> sellEtf(@PathVariable("etfId") Long etfId,
                                                                      @RequestParam("share_count") int shareCount,
                                                                      @RequestParam("studentId") Long studentId);

    @Operation(summary = "(교사/학생) 변동률", description = "💡 주식 변동률 조회")
    public ResponseEntity<ApiResponse<EtfDTO>> updateEtfPrice(
            @PathVariable("etfId") Long etfId);

//    @Operation(summary = "ETF 등록", description = "💡 새로운 ETF를 등록합니다.")
//    @PostMapping("/create")
//    public ResponseEntity<ApiResponse<EtfDTO>> addEtf(@RequestParam Long studentId,
//                                                      @RequestParam List<Long> stockIds);






}
