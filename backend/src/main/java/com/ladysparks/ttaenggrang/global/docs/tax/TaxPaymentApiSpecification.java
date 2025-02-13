package com.ladysparks.ttaenggrang.global.docs.tax;

import com.ladysparks.ttaenggrang.domain.tax.dto.OverdueTaxPaymentDTO;
import com.ladysparks.ttaenggrang.domain.tax.dto.TaxPaymentDTO;
import com.ladysparks.ttaenggrang.domain.tax.dto.TeacherStudentTaxPaymentDTO;
import com.ladysparks.ttaenggrang.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "[교사/학생] 국세청", description = "세금 납부 내역 관련 API")
public interface TaxPaymentApiSpecification {

    @Operation(summary = "(교사/학생) 세금 납부 내역 [조회]", description = """
            💡 특정 학생의 기간 별 세금 납부 내역을 조회합니다.
            
            ---
            
            **[ 요청 값 ]**
            - **studentId** : 학생 ID (선택)
            - **startDate** : 시작일 (선택)
            - **endDate** : 종료일 (선택)
            
            **[ 응답 필드 ]**
            - **id** : 세금 납부 ID
            - **studentId** : 학생 ID
            - **taxId** : 세금 ID
            - **paymentDate** : 납부일
            - **taxName** : 세금명
            - **taxDescription** : 세금 설명
            - **taxRate** : 세율
            - **amount** : 납부액
            - **overdue** : 미납 여부
            
            ---
            
            **[ 설명 ]**
            - 학생이 특정 기간에 납부한 세금 정보를 조회할 수 있습니다.
            - 학생이 조회하는 경우 `studentId`를 입력하지 않아도 됩니다.
            - 교사가 조회하는 경우 `studentId`를 입력해야 합니다.
            - `startDate`, `endDate`를 모두 입력하지 않을 경우 **이번 달** 세금 납부 내역을 반환합니다.
            - `startDate`(시작일)만 없으면 `endDate`(종료일) 이전 전체 내역을 조회합니다.
            - `endDate`(종료일)만 없으면 `startDate`(시작일) 이후 전체 내역을 조회합니다.
            - `startDate`(시작일)은 `endDate`(종료일)보다 이후일 수 없습니다.
            """)
    ResponseEntity<ApiResponse<List<TaxPaymentDTO>>> taxPaymentList(
            @RequestParam(required = false) Long studentId,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate);

    @Operation(summary = "(교사) 전체 학생 세금 납부 내역 [조회]", description = """
            💡 특정 교사가 담당하는 학생들의 세금 납부 내역을 조회합니다.
            
            ---
            
            **[ 응답 필드 ]**
            - **studentId** : 학생 ID
            - **studentName** : 학생 이름
            - **totalAmount** : 총 납부 금액
            """)
    ResponseEntity<ApiResponse<List<TeacherStudentTaxPaymentDTO>>> taxPaymentListByTeacher();

    @Operation(summary = "(교사/학생) 총 납부 금액 [조회]", description = """
            💡 특정 학생의 총 납부 금액을 조회합니다.
            
            ---
            
            **[ 요청 값 ]**
            - **studentId** : 학생 ID (선택)
            
            **[ 응답 필드 ]**
            - **studentId** : 학생 ID
            - **studentName** : 학생 이름
            - **totalAmount** : 총 납부 금액
            
            ---
            
            **[ 설명 ]**
            - 학생이 조회하는 경우 `studentId`를 입력하지 않아도 됩니다.
            - 교사가 조회하는 경우 `studentId`를 입력해야 합니다.
            """)
    ResponseEntity<ApiResponse<TeacherStudentTaxPaymentDTO>> taxPaymentByStudent(@RequestParam(required = false) Long studentId);

    @Operation(summary = "(학생) 미납 세금 내역 [조회]", description = """
            💡 특정 학생의 미납 세금 내역을 조회합니다.
            
            ---
  
            **[ 응답 필드 ]**
            - **taxName** : 세금명
            - **description** : 미납 사유
            - **totalAmount** : 미납된 세금 총합
            
            ---
            
            **[ 설명 ]**
            - `taxName`(미납된 세금명) '벌금'이라는 제목으로 통일합니다.
            - `description`(미납 사유)는 '부가세, 소득세, 주민세'와 같이 미납된 세금명들이 나열됩니다.
            - 미납된 세금을 일괄적으로 납부하기 위해서 총합을 계산합니다.
            """)
    ResponseEntity<ApiResponse<OverdueTaxPaymentDTO>> OverdueTaxPaymentList();

    @Operation(summary = "(학생) 미납 세금 납부 [등록]", description = """
            💡 학생이 미납된 모든 세금을 일괄 납부합니다.
            
            ---
    
            **[ 응답 필드 ]**
            - **updatedCount** : 업데이트된 세금 건수
            
            ---
            
            **[ 설명 ]**
            - 해당 학생의 모든 미납 세금을 완납(isOverdue = false)으로 업데이트합니다.
            - 미납 세금을 납부하면 은행 계좌에 `FINE`(벌금) 타입의 거래가 등록되고 계좌 잔액에서 출금됩니다.
            """)
    ResponseEntity<ApiResponse<Integer>> overdueTaxPaymentsClear();

//    @Operation(summary = "세금 유형별 납부 내역 [조회]", description = """
//            💡 학급 내 특정 세금 유형에 대한 납부 내역을 조회합니다.
//
//            **[ 응답 필드 ]**
//            - **id** : 세금 납부 ID
//            - **studentId** : 학생 ID
//            - **taxId** : 세금 ID
//            - **amount** : 납부 금액
//            - **paymentDate** : 납부일
//            - **status** : 납부 상태
//            - **createdAt** : 납부 생성일
//            """)
//    ResponseEntity<ApiResponse<List<TaxPaymentDTO>>> taxPaymentListByTax(@RequestParam Long taxId);

}