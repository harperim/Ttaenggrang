package com.ladysparks.ttaenggrang.domain.etf.controller;

import com.ladysparks.ttaenggrang.domain.etf.dto.EtfDTO;
import com.ladysparks.ttaenggrang.domain.etf.dto.EtfSummaryDTO;
import com.ladysparks.ttaenggrang.domain.etf.dto.EtfTransactionDTO;
import com.ladysparks.ttaenggrang.domain.etf.service.EtfService;
import com.ladysparks.ttaenggrang.domain.stock.dto.StockDTO;
import com.ladysparks.ttaenggrang.domain.stock.dto.StockSummaryDTO;
import com.ladysparks.ttaenggrang.domain.student.service.StudentService;
import com.ladysparks.ttaenggrang.domain.teacher.service.TeacherService;
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
public class EtfController  {
    private final EtfService etfService; // StockService 주입
    private final StudentService studentService;
    private final TeacherService teacherService;

//    //ETF 생성
//    @PostMapping("/create")
//    public ResponseEntity<ApiResponse<EtfDTO>> addEtf(@RequestParam Long studentId,
//                                                      @RequestParam List<Long> stockIds) {
//        // ETF 생성 서비스 호출
//        EtfDTO createdEtfDTO = etfService.createETF(studentId, stockIds);
//
//        // ETF 생성 후 성공적인 응답 반환
//        return ResponseEntity.status(HttpStatus.CREATED)
//                .body(ApiResponse.created(createdEtfDTO));
//    }

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


}
