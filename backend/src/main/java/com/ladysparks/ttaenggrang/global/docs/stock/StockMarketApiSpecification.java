package com.ladysparks.ttaenggrang.global.docs.stock;

import com.ladysparks.ttaenggrang.domain.stock.dto.*;
import com.ladysparks.ttaenggrang.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@Tag(name = "[교사/학생] 주식 시장", description = "주식 시장 개장/폐장 관련 API")
public interface StockMarketApiSpecification {

//    @Operation(summary = "(교사/학생) 주식 시장 버튼(True or False) 조회", description = "💡주식시장 버튼(True or False) 조회 합니다.")
//    ResponseEntity<Boolean> getMarketStatus();

    @Operation(summary = "(교사) 주식 시장 개장/폐장 제어 [수정]", description = """
            💡 교사가 수동으로 주식 시장을 개장 및 폐장합니다.
            
            ---
            
            **[ 요청 값 ]**
            - **open**: 개장 여부
            
            **[ 응답 필드 ]**
            - **teacherId** : 교사 ID
            - **isMarketOpen** : 주식 시장 개장 여부(주식 거래 가능 여부)
            - **isTeacherOn** : 교사 On/Off 설정
            
            ---
            
            **[ 설명 ]**
            - **평일 9시 ~ 17시** 사이에는 교사의 주식 시장 개장/폐장 설정이 곧바로 적용됩니다.
            - 그 외 시간에 교사가 개장(On)하려는 경우, 시장을 개장할 수 없고 `"개장이 불가능한 시간입니다."`라는 문구와 함께 에러가 발생됩니다.
            """)
    ResponseEntity<ApiResponse<StockMarketStatusDTO>> setTeacherOnOff(@RequestParam @Parameter(description = "주식 시장 개장") boolean open) throws BadRequestException;

    @Operation(summary = "(교사/학생) 현재 주식 거래 가능 여부 [조회]", description = """
            💡 현재 주식 거래 가능 여부를 조회합니다. (주식 시장 개장 여부 + 교사 설정)
            
            ---
            
            **[ 응답 필드 ]**
            - **available** : 주식 거래 가능 여부
            
            ---
            
            **[ 설명 ]**
            - **주식 시장 개장 여부와 교사 설정**에 따른 주식 거래 가능 여부를 반환합니다.
            """)
    ResponseEntity<ApiResponse<Map<String, Boolean>>> isTransactionAvailable();

}