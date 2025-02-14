package com.ladysparks.ttaenggrang.global.docs.bank;

import com.ladysparks.ttaenggrang.domain.bank.dto.SavingsSubscriptionDTO;
import com.ladysparks.ttaenggrang.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Tag(name = "[학생] 적금 가입", description = "적금 가입 내역 관련 API")
public interface SavingsSubscriptionApiSpecification {

    @Operation(summary = "(학생) 적금 가입 [등록]", description = """
            💡 학생이 적금 상품에 가입합니다.
            
            ---
            
            **[ 요청 필드 ]**
            - **savingsProductId** : 적금 상품 ID
            - **depositDayOfWeek** : 납입 요일
                - **"MONDAY"**
                - **"TUESDAY"**
                - **"WEDNESDAY"**
                - **"THURSDAY"**
                - **"FRIDAY"**
                - **"SATURDAY"**
                - **"SUNDAY"**

            ---
            
            **[ 설명 ]**
            - 학생이 선택한 요일에 자동으로 적금을 납입합니다.
            """)
    ResponseEntity<ApiResponse<SavingsSubscriptionDTO>> savingsSubscriptionAdd(@RequestBody SavingsSubscriptionDTO savingsSubscriptionDTO);

    @Operation(summary = "(학생) 적금 가입 내역 [전체 조회]", description = """
            💡 학생의 적금 가입 내역을 조회합니다.
            
            ---
            
            **[ 응답 필드 ]**
            - **id** : 적금 가입 ID
            - **savingsProductId** : 적금 상품 ID
            - **studentId** : 학생 ID
            - **startDate** : 납입 시작일
            - **endDate** : 납입 종료일
            - **status** : 가입 상태
            - **depositDayOfWeek** : 납입 요일
                - **"MONDAY"**
                - **"TUESDAY"**
                - **"WEDNESDAY"**
                - **"THURSDAY"**
                - **"FRIDAY"**
                - **"SATURDAY"**
                - **"SUNDAY"**
            - **createdAt** : 적금 가입일
            - **depositSchedule** : 납입 일정
            """)
    ResponseEntity<ApiResponse<List<SavingsSubscriptionDTO>>> savingsSubscriptionList();

}