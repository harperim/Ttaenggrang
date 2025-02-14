package com.ladysparks.ttaenggrang.global.docs.stock;

import com.ladysparks.ttaenggrang.domain.stock.dto.StockTransactionDTO;
import com.ladysparks.ttaenggrang.domain.stock.dto.StudentStockDTO;
import com.ladysparks.ttaenggrang.domain.stock.dto.StockTransactionResponseDTO;
import com.ladysparks.ttaenggrang.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Tag(name = "[학생] 주식 거래", description = "주식 거래 관련 API")
public interface StockTransactionApiSpecification {

    @Operation(summary = "(학생) 주식 매수", description = "💡 주식 ID와 수량으로 주식을 매수합니다.")
    ResponseEntity<ApiResponse<StockTransactionDTO>> buyStock(@PathVariable("stockId") Long stockId,
                                                                     @RequestParam("share_count") int shareCount,
                                                                     @RequestParam("studentId") Long studentId);

    @Operation(summary = "(학생) 주식 매도", description = "💡 주식 ID와 수량으로 주식을 매도합니다.")
    ResponseEntity<ApiResponse<StockTransactionDTO>> sellStock(@PathVariable("stockId") Long stockId,
                                                                     @RequestParam("share_count") int shareCount,
                                                                     @RequestParam("studentId") Long studentId);

    @Operation(summary = "(학생) 학생 거래내역 조회", description = """
            💡 학생의 매수/매도에 대한 거래 내역을 조회 합니다
            """)
    ResponseEntity<List<StockTransactionResponseDTO>> getStudentTransactions();

}