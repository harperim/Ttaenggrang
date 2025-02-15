package com.ladysparks.ttaenggrang.global.docs.teacher;

import com.ladysparks.ttaenggrang.domain.teacher.dto.SingleStudentCreateDTO;
import com.ladysparks.ttaenggrang.domain.student.dto.StudentResponseDTO;
import com.ladysparks.ttaenggrang.domain.teacher.dto.StudentSavingsSubscriptionDTO;
import com.ladysparks.ttaenggrang.domain.teacher.dto.StudentStockTransactionDTO;
import com.ladysparks.ttaenggrang.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "[교사] 우리 반 관리", description = "우리 반 관리 기능 API")
public interface TeacherStudentApiSpecificaion {

    @Operation(summary = "(교사) 학생 계정 단일 생성", description = """
            💡 교사가 학생 계정을 1개만 생성합니다.
            
            - **username** : 학생 ID
            - **password** : 비밀번호
            - **teacher** : 학생의 담임 선생님 정보
            - 교사만 생성 가능
            """)
    @PostMapping("/single-create")
    ResponseEntity<ApiResponse<StudentResponseDTO>> createStudent(@RequestBody @Valid SingleStudentCreateDTO singleStudentCreateDTO);

    @Operation(summary = "(교사) 학생 계정 복수 생성", description = """
            💡 교사가 여러 개의 학생 계정을 생성합니다.
            
            - **baseId** : 학생 계정의 base ID
            - **studentCount** : 우리 반 학생 수
            - **예시**
                - **baseId** : student
                - **studentCount** : 10
                - student1, ..., student10 까지 학생 계정 생성
            - 교사만 생성 가능
            """)
    @PostMapping("/quick-create")
    ResponseEntity<ApiResponse<List<StudentResponseDTO>>> createStudents(
            @Parameter(description = "학생 계정의 base ID") @RequestParam("baseId") String baseId,
            @Parameter(description = "생성할 학생 계정 수") @RequestParam("studentCount") int studentCount,
            @Parameter(description = "학생 이름이 포함된 파일 (CSV 또는 XLSX)") @RequestPart("file") MultipartFile file
    );

    @Operation(summary = "(교사) 우리 반 학생 [전체 조회]", description = """
            💡 교사가 우리 반 전체 학생 목록을 조회합니다.
            
            - **username** : 학생 ID
            - **name** : 학생 실명
            - **profileImage** : 학생 프로필 이미지 경로
            - **teacher** : 학생의 담임 선생님 정보
            - **bankAccount** : 학생의 계좌 정보
            """)
    ResponseEntity<ApiResponse<List<StudentResponseDTO>>> getMyClassStudents();

    @Operation(summary = "(교사) 우리 반 학생 [상세 조회]", description = """
            💡 교사가 특정 학생 정보를 조회합니다.
            
            - **username** : 학생 ID
            - **name** : 학생 실명
            - **profileImage** : 학생 프로필 이미지 경로
            - **teacher** : 학생의 담임 선생님 정보
            - **bankAccount** : 학생의 계좌 정보
            """)
    ResponseEntity<ApiResponse<StudentResponseDTO>> getStudentById(@PathVariable Long studentId);

    @Operation(summary = "(교사) 학생 은행 가입 상품 내역 [조회]", description = """
            💡 교사가 특정 학생의 은행 가입 상품 내역을 조회합니다.

            ---

            **[ 요청 값 ]**
            - **studentId** : 조회할 학생 ID

            **[ 응답 필드 ]**
            - **subscriptionDate** : 적금 가입 날짜
            - **savingsName** : 적금 상품명
            - **amount** : 월 납입 금액
            - **interest** : 이자율
            - **totalAmount** : 현재 총 납입 금액

            ---

            **[ 설명 ]**
            - 특정 학생(`studentId`)의 적금 가입 내역을 조회합니다.
            - 적금 가입 후 현재까지의 납입 총액이 포함됩니다.
            """)
    ResponseEntity<ApiResponse<List<StudentSavingsSubscriptionDTO>>> studentSavingsSubscriptionList(@PathVariable Long studentId);

    @Operation(summary = "(교사) 학생 보유 주식 현황 [조회]", description = """
            💡 교사가 특정 학생이 보유한 주식 현황을 조회합니다.
    
            ---
    
            **[ 요청 값 ]**
            - **studentId** : 조회할 학생의 ID
    
            **[ 응답 필드 ]**
            - **stockName** : 주식명
            - **quantity** : 보유 수량
            - **currentTotalPrice** : 현재가 (총 평가 금액)
            - **purchasePrice** : 주당 구매 가격
            - **priceChangeRate** : 주가 변동률 (%)
    
            ---
    
            **[ 설명 ]**
            - 교사가 특정 학생의 주식 보유 현황을 조회할 수 있습니다.
            - 주가 변동률은 `(현재가 - 구매가) / 구매가 * 100` 으로 계산됩니다.
            """)
    ResponseEntity<ApiResponse<List<StudentStockTransactionDTO>>> studentStockTransactionList(@PathVariable Long studentId);

}
