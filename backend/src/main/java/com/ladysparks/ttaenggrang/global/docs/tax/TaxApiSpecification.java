package com.ladysparks.ttaenggrang.global.docs.tax;

import com.ladysparks.ttaenggrang.domain.tax.dto.TaxDTO;
import com.ladysparks.ttaenggrang.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Tag(name = "[교사] 국세청", description = "세금 관련 API")
public interface TaxApiSpecification {

    @Operation(summary = "(교사) 세금 [등록]", description = """
            💡 교사가 세금 정보를 등록합니다.
            
            ---
            
            **[ 응답 필드 ]**
            - **taxName** : 세금명
            - **taxRate** : 세율 (%)
            - **taxDescription** : 세금 설명
            """)
    ResponseEntity<ApiResponse<TaxDTO>> taxAdd(@RequestBody @Valid TaxDTO taxDTO);

    @Operation(summary = "(교사/학생) 세금 [전체 조회]", description = """
            💡 교사가 설정한 국가의 직접 추가한 세금 정보를 조회합니다.
            
            ---
            
            **[ 응답 필드 ]**
            - **taxName** : 세금명
            - **taxRate** : 세율 (%)
            - **taxDescription** : 세금 설명
            """)
    ResponseEntity<ApiResponse<List<TaxDTO>>> taxList();

}
