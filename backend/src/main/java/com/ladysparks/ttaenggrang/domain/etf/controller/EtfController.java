package com.ladysparks.ttaenggrang.domain.etf.controller;

import com.ladysparks.ttaenggrang.domain.etf.dto.EtfDTO;
import com.ladysparks.ttaenggrang.domain.etf.dto.EtfSummaryDTO;
import com.ladysparks.ttaenggrang.domain.etf.dto.EtfTransactionDTO;
import com.ladysparks.ttaenggrang.domain.etf.service.EtfService;
import com.ladysparks.ttaenggrang.domain.etf.service.EtfTransactionService;
import com.ladysparks.ttaenggrang.domain.stock.dto.StockDTO;
import com.ladysparks.ttaenggrang.domain.stock.dto.StockSummaryDTO;
import com.ladysparks.ttaenggrang.domain.student.service.StudentService;
import com.ladysparks.ttaenggrang.domain.teacher.dto.StudentEtfTransactionDTO;
import com.ladysparks.ttaenggrang.domain.teacher.dto.StudentStockTransactionDTO;
import com.ladysparks.ttaenggrang.domain.teacher.service.TeacherService;
import com.ladysparks.ttaenggrang.global.docs.stock.EtfApiSpecification;
import com.ladysparks.ttaenggrang.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/etfs")
public class EtfController implements EtfApiSpecification {
    private final EtfService etfService; // StockService 주입
    private final StudentService studentService;
    private final TeacherService teacherService;
    private final EtfTransactionService etfTransactionService;


// ETF 생성 API
    @PostMapping("/create") // POST 요청으로 ETF 생성
    public ResponseEntity<ApiResponse<EtfDTO>> createETF(
            @RequestParam String name, // ETF 이름
            @RequestParam String category, // ETF 카테고리
            @RequestParam List<Long> selectedStockIds) { // ETF에 포함될 주식 ID 목록

        // ETF 생성 서비스 호출
        EtfDTO createdEtfDTO = etfService.createETF(name, category, selectedStockIds);

        // 성공 응답 생성 및 ResponseEntity로 반환
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(createdEtfDTO)); // API 응답을 "Resource created successfully." 메시지와 함께 반환
}

    //Etf  목록 전체 조회
    @GetMapping
    public ResponseEntity<ApiResponse<List<EtfDTO>>> getEtfList() {
        Optional<Long> studentId = studentService.getOptionalCurrentStudentId();
        Long teacherId = studentId.isPresent() ? studentService.findTeacherIdByStudentId(studentId.get()) : teacherService.getCurrentTeacherId();
        List<EtfDTO> result = etfService.findEtfs(teacherId); // 모든 주식 정보를 반환
        return ResponseEntity.ok(ApiResponse.success(result)); // HTTP 200 OK와 함께 결과 반환
    }

    // 주식 요약 목록 전체 조회
    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<List<EtfSummaryDTO>>> getEtfSummaryList() {
        Optional<Long> studentId = studentService.getOptionalCurrentStudentId();
        Long teacherId = studentId.isPresent() ? studentService.findTeacherIdByStudentId(studentId.get()) : teacherService.getCurrentTeacherId();
        List<EtfSummaryDTO> result = etfService.getEtfSummaryList(teacherId); // 모든 주식 정보를 반환
        return ResponseEntity.ok(ApiResponse.success(result)); // HTTP 200 OK와 함께 결과 반환
    }

    // 학생 보유 주식 조회
    @GetMapping("/buy")
    public ResponseEntity<ApiResponse<List<StudentEtfTransactionDTO>>> getStudentEtfs() {
        Long studentId = studentService.getCurrentStudentId();  //학생 아이디 알아서 조회 해줌
        List<StudentEtfTransactionDTO> stockList = etfTransactionService.findStudentEtfTransactionsByStudentId(studentId);
        return ResponseEntity.ok(ApiResponse.success(stockList));
    }


}
