package com.ladysparks.ttaenggrang.domain.stock.controller;

import com.ladysparks.ttaenggrang.domain.stock.dto.StockDTO;
import com.ladysparks.ttaenggrang.domain.stock.dto.StockSummaryDTO;
import com.ladysparks.ttaenggrang.domain.stock.dto.StudentStockDTO;
import com.ladysparks.ttaenggrang.domain.stock.service.StockService;
import com.ladysparks.ttaenggrang.domain.student.service.StudentService;
import com.ladysparks.ttaenggrang.domain.teacher.service.TeacherService;
import com.ladysparks.ttaenggrang.global.docs.stock.StockApiSpecification;
import com.ladysparks.ttaenggrang.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/stocks")
public class StockController implements StockApiSpecification {

    private final StockService stockService;
    private final StudentService studentService;
    private final TeacherService teacherService;

    // 주식 등록
    @PostMapping
    public ResponseEntity<ApiResponse<StockDTO>> addStock(@RequestBody StockDTO stockDTO) {
        // 주식 등록 서비스 호출
        StockDTO createdStockDTO = stockService.registerStock(stockDTO);

        // 주식 등록 후 성공적인 응답 반환
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(createdStockDTO));
    }

    // 주식 요약 목록 전체 조회
    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<List<StockSummaryDTO>>> getStockSummaryList() {
        Optional<Long> studentId = studentService.getOptionalCurrentStudentId();
        Long teacherId = studentId.isPresent() ? studentService.findTeacherIdByStudentId(studentId.get()) : teacherService.getCurrentTeacherId();
        List<StockSummaryDTO> result = stockService.getStockSummaryList(teacherId); // 모든 주식 정보를 반환
        return ResponseEntity.ok(ApiResponse.success(result)); // HTTP 200 OK와 함께 결과 반환
    }

    // 주식 목록 전체 조회
    @GetMapping
    public ResponseEntity<ApiResponse<List<StockDTO>>> getStockList() {
        Optional<Long> studentId = studentService.getOptionalCurrentStudentId();
        Long teacherId = studentId.isPresent() ? studentService.findTeacherIdByStudentId(studentId.get()) : teacherService.getCurrentTeacherId();
        List<StockDTO> result = stockService.findStocks(teacherId); // 모든 주식 정보를 반환
        return ResponseEntity.ok(ApiResponse.success(result)); // HTTP 200 OK와 함께 결과 반환
    }

    // 주식 상세 조회
    @GetMapping("/{stockId}")
    public ResponseEntity<ApiResponse<StockDTO>> getStock(@PathVariable("stockId") Long stockId) {
        Optional<StockDTO> result = stockService.findStock(stockId);

        // 값이 없으면 404 Not Found 반환
        if (result.isPresent()) {
            return ResponseEntity.ok(ApiResponse.success(result.get())); // 값이 있으면 200 OK와 함께 결과 반환
        } else {
            return ResponseEntity.notFound().build(); // 값이 없으면 404 Not Found 반환
        }
    }

    // 학생 보유 주식 조회
    @GetMapping("/buy")
    public ResponseEntity<ApiResponse<List<StudentStockDTO>>> getStudentStocks() {
        Long studentId = studentService.getCurrentStudentId();
        List<StudentStockDTO> stockList = stockService.getStudentStocks(studentId);
        return ResponseEntity.ok(ApiResponse.success(stockList));
    }

}