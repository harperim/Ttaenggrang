package com.ladysparks.ttaenggrang.global.docs.stock;

import com.ladysparks.ttaenggrang.domain.etf.dto.EtfDTO;
import com.ladysparks.ttaenggrang.domain.etf.dto.EtfSummaryDTO;
import com.ladysparks.ttaenggrang.domain.teacher.dto.StudentEtfTransactionDTO;
import com.ladysparks.ttaenggrang.domain.teacher.dto.StudentStockTransactionDTO;
import com.ladysparks.ttaenggrang.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;



import java.util.List;
@Tag(name = "[교사/학생] ETF", description = "ETF API")
public interface EtfApiSpecification {

    @Operation(summary = "(교사/학생) ETF 전체 조회", description = "💡 전체 ETF을 조회합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<List<EtfDTO>>> getEtfList();

    @Operation(summary = "(교사/학생) ETF 요약 조회", description = "💡 ETF 요약 조회 합니다.")
    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<List<EtfSummaryDTO>>> getEtfSummaryList();

    @Operation(summary = "(학생) 보유 주식 [전체 조회]", description = """
            💡 학생이 보유한 주식 목록을 조회합니다.
            """)
    @GetMapping("/buy")
    public ResponseEntity<ApiResponse<List<StudentEtfTransactionDTO>>> getStudentEtfs() ;





}
