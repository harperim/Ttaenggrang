package com.ladysparks.ttaenggrang.domain.stock.controller;

import com.ladysparks.ttaenggrang.domain.etf.dto.EtfHistoryDTO;
import com.ladysparks.ttaenggrang.domain.etf.service.EtfHistroryService;
import com.ladysparks.ttaenggrang.domain.stock.dto.ChangeResponseDTO;
import com.ladysparks.ttaenggrang.domain.stock.dto.StockHistoryDTO;
import com.ladysparks.ttaenggrang.domain.stock.service.StockHistoryService;
import com.ladysparks.ttaenggrang.domain.student.service.StudentService;
import com.ladysparks.ttaenggrang.domain.teacher.service.TeacherService;
import com.ladysparks.ttaenggrang.global.docs.stock.StockHistoryApiSpecification;
import com.ladysparks.ttaenggrang.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/stock-history")
public class StockHistoryController implements StockHistoryApiSpecification {

    private final StockHistoryService stockHistoryService;
    private final TeacherService teacherService;
    private final StudentService studentService;
    private final EtfHistroryService etfHistroryService;

    // 모든 주식 가격 변동 이력 조회
    @GetMapping("/stocks")
    public ResponseEntity<ApiResponse<Map<Long, List<StockHistoryDTO>>>> getLast5DaysStockHistory() {
        Optional<Long> studentId = studentService.getOptionalCurrentStudentId();
        Long teacherId = studentId.isPresent() ? studentService.findTeacherIdByStudentId(studentId.get()) : teacherService.getCurrentTeacherId();
        Map<Long, List<StockHistoryDTO>> historyMap = stockHistoryService.getLast5WeekdaysStockHistory(teacherId);
        return ResponseEntity.ok(ApiResponse.success(historyMap));
    }

    // 2) ETF 가격 변동 이력 조회
    @GetMapping("/etfs")
    public ResponseEntity<ApiResponse<Map<Long, List<EtfHistoryDTO>>>> getLast5DaysEtfHistory() {
        Optional<Long> studentId = studentService.getOptionalCurrentStudentId();
        Long teacherId = studentId.isPresent()
                ? studentService.findTeacherIdByStudentId(studentId.get())
                : teacherService.getCurrentTeacherId();

        // ETF 전용 로직 (EtfHistoryService를 통해 조회)
        Map<Long, List<EtfHistoryDTO>> etfHistoryMap =
                etfHistroryService.getLast5WeekdaysEtfHistory(teacherId);

        return ResponseEntity.ok(ApiResponse.success(etfHistoryMap));
    }

}