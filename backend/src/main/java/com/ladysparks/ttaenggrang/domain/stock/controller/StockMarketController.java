package com.ladysparks.ttaenggrang.domain.stock.controller;

import com.ladysparks.ttaenggrang.domain.stock.dto.StockMarketStatusDTO;
import com.ladysparks.ttaenggrang.domain.stock.service.StockMarketStatusService;
import com.ladysparks.ttaenggrang.domain.student.service.StudentService;
import com.ladysparks.ttaenggrang.domain.teacher.service.TeacherService;
import com.ladysparks.ttaenggrang.global.docs.stock.StockMarketApiSpecification;
import com.ladysparks.ttaenggrang.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/stock-market")
public class StockMarketController implements StockMarketApiSpecification {

    private final StockMarketStatusService stockMarketService;
    private final TeacherService teacherService;
    private final StudentService studentService;

//    @GetMapping("/on-off")
//    public ResponseEntity<ApiResponse<Boolean>> getTeacherOnOff() {
//        Long teacherId = teacherService.getCurrentTeacherId();
//        boolean isMarketActive = stockMarketService.getTeacherOnOff(teacherId);
//        return ResponseEntity.ok(isMarketActive);
//    }

    // 주식 시장 활성화/비활성화 설정 (선생님이 버튼으로 설정)
    @GetMapping
    public ResponseEntity<ApiResponse<StockMarketStatusDTO>> setTeacherOnOff(@RequestParam @Parameter(description = "주식 시장 활성화") boolean open) throws BadRequestException {
        Long teacherId = teacherService.getCurrentTeacherId();
        return ResponseEntity.ok(ApiResponse.success(stockMarketService.setTeacherOnOff(teacherId, open)));
    }

    // 현재 주식 거래 가능 여부 조회 (시장 개장 여부 + 교사 설정 여부)
    @GetMapping("/status")
    public ResponseEntity<ApiResponse<Map<String, Boolean>>> isTransactionAvailable() {
        Optional<Long> studentId = studentService.getOptionalCurrentStudentId();
        Long teacherId = studentId.isPresent() ? studentService.findTeacherIdByStudentId(studentId.get()) : teacherService.getCurrentTeacherId();

        Map<String, Boolean> response = new HashMap<>();
        response.put("available", stockMarketService.isMarketOpen(teacherId));
        return ResponseEntity.ok(ApiResponse.success(response));
    }

}