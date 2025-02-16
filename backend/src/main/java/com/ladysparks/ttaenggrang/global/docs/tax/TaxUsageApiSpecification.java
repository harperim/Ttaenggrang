package com.ladysparks.ttaenggrang.global.docs.tax;

import com.ladysparks.ttaenggrang.domain.tax.dto.TaxUsageDTO;
import com.ladysparks.ttaenggrang.domain.tax.entity.TaxUsage;
import com.ladysparks.ttaenggrang.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Tag(name = "[교사] 국세청 국고 사용", description = "국고 사용 관련 API")
public interface TaxUsageApiSpecification {

    @Operation(summary = "(교사) 국고 사용 [등록]", description = """
            💡 교사가 국세를 사용한 내역을 기록합니다.

            ---

            **[ 요청 필드 ]**
            - **name** : 사용 내역 (세금명)
            - **amount** : 사용 금액
            - **description** : 설명

            **[ 응답 필드 ]**
            - **name** : 사용 내역 (세금명)
            - **amount** : 사용 금액
            - **description** : 설명
            - **usageDate** : 사용 날짜

            ---

            **[ 설명 ]**
            - 선생님 계정의 국가 정보를 기준으로 국세 사용을 등록합니다.
            - 국세 사용 시 자동으로 국고 금액에서 차감됩니다.
            - 국고 금액이 부족하면 예외가 발생합니다.
            """)
    ResponseEntity<ApiResponse<TaxUsageDTO>> taxUsage(@RequestBody TaxUsageDTO taxUsageDTO);

    @Operation(summary = "(교사) 국세 사용 내역 [전체 조회]", description = """
            💡 특정 국가의 모든 국세 사용 내역을 조회합니다.
            
            ---

            **[ 요청 값 ]**
            - 없음

            **[ 응답 필드 ]**
            - **name** : 사용 내역 (세금명)
            - **amount** : 사용 금액
            - **description** : 설명
            - **usageDate** : 사용 날짜

            ---

            **[ 설명 ]**
            - 로그인한 교사가 생성한 국가의 모든 국세 사용 내역을 반환합니다.
            """)
    ResponseEntity<ApiResponse<List<TaxUsageDTO>>> taxUsageList();

}
