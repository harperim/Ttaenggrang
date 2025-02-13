package com.ladysparks.ttaenggrang.domain.tax.controller;

import com.ladysparks.ttaenggrang.domain.student.service.StudentService;
import com.ladysparks.ttaenggrang.domain.tax.dto.OverdueTaxPaymentDTO;
import com.ladysparks.ttaenggrang.domain.tax.dto.TaxPaymentDTO;
import com.ladysparks.ttaenggrang.domain.tax.dto.TeacherStudentTaxPaymentDTO;
import com.ladysparks.ttaenggrang.domain.tax.service.TaxPaymentService;
import com.ladysparks.ttaenggrang.domain.teacher.service.TeacherService;
import com.ladysparks.ttaenggrang.global.docs.tax.TaxPaymentApiSpecification;
import com.ladysparks.ttaenggrang.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/tax-payments/")
@RequiredArgsConstructor
public class TaxPaymentController implements TaxPaymentApiSpecification {

    private final TaxPaymentService taxPaymentService;
    private final TeacherService teacherService;
    private final StudentService studentService;

    // 세금 납부 등록
//    @PostMapping
//    public ResponseEntity<ApiResponse<TaxPaymentDTO>> taxPaymentAdd(@RequestBody @Valid TaxPaymentDTO requestDTO) {
//        return ResponseEntity.ok(ApiResponse.created(taxPaymentService.addTaxPayment(requestDTO)));
//    }

    // 특정 학생의 세금 납부 내역 조회
    @GetMapping("/period")
    public ResponseEntity<ApiResponse<List<TaxPaymentDTO>>> taxPaymentList(
            @RequestParam(required = false) Long studentId,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate) {
        if (studentId == null) {
            studentId = studentService.getCurrentStudentId();
        }
        List<TaxPaymentDTO> taxPayments = taxPaymentService.getTaxPaymentsByPeriod(studentId, startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success(taxPayments));
    }

    /**
     * 특정 교사의 학생들의 세금 납부 내역 조회
     */
    @GetMapping("/students")
    public ResponseEntity<ApiResponse<List<TeacherStudentTaxPaymentDTO>>> taxPaymentListByTeacher() {
        Long teacherId = teacherService.getCurrentTeacherId();
        return ResponseEntity.ok(ApiResponse.success(taxPaymentService.getStudentTaxPaymentListByTeacher(teacherId)));
    }

    /**
     * 특정 학생의 총 납부 금액
     */
    @GetMapping("/total-amount")
    public ResponseEntity<ApiResponse<TeacherStudentTaxPaymentDTO>> taxPaymentByStudent(@RequestParam(required = false) Long studentId) {
        if (studentId == null) {
            studentId = studentService.getCurrentStudentId();
        }
        return ResponseEntity.ok(ApiResponse.success(taxPaymentService.getStudentTaxPaymentByStudent(studentId)));
    }

    @GetMapping("/overdue")
    public ResponseEntity<ApiResponse<OverdueTaxPaymentDTO>> OverdueTaxPaymentList() {
        Long studentId = studentService.getCurrentStudentId();
        OverdueTaxPaymentDTO overdueTaxPayments = taxPaymentService.getOverdueTaxPayments(studentId);
        return ResponseEntity.ok(ApiResponse.success(overdueTaxPayments));
    }

    @PutMapping("/overdue/clear")
    public ResponseEntity<ApiResponse<Integer>> overdueTaxPaymentsClear() {
        Long studentId = studentService.getCurrentStudentId();
        int updatedCount = taxPaymentService.clearOverdueTaxPayments(studentId);
        return ResponseEntity.ok(ApiResponse.success(updatedCount));
    }

    // 특정 세금 유형에 대한 납부 내역 조회
//    @GetMapping("/taxes/{taxId}")
//    public ResponseEntity<ApiResponse<List<TaxPaymentDTO>>> taxPaymentListByTax(@PathVariable Long taxId) {
//        return ResponseEntity.ok(ApiResponse.success(taxPaymentService.findTaxPaymentsByTax(taxId)));
//    }

}