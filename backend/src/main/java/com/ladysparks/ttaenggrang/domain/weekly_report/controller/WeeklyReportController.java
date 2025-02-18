package com.ladysparks.ttaenggrang.domain.weekly_report.controller;

import com.ladysparks.ttaenggrang.domain.student.service.StudentService;
import com.ladysparks.ttaenggrang.domain.weekly_report.dto.StudentFinancialSummaryDTO;
import com.ladysparks.ttaenggrang.domain.weekly_report.dto.WeeklyFinancialSummaryDTO;
import com.ladysparks.ttaenggrang.domain.weekly_report.service.FastApiService;
import com.ladysparks.ttaenggrang.domain.weekly_report.service.WeeklyFinancialSummaryService;
import com.ladysparks.ttaenggrang.global.docs.weekly.WeeklyReportApiSpecification;
import com.ladysparks.ttaenggrang.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "https://i12d107.p.ssafy.io")
@RequestMapping("/weekly-report")
public class WeeklyReportController implements WeeklyReportApiSpecification {

    private final WeeklyFinancialSummaryService weeklyFinancialSummaryService;
    private final StudentService studentService;
    private final FastApiService fastApiService;

    @GetMapping
    public ResponseEntity<ApiResponse<WeeklyFinancialSummaryDTO>> weeklyReportDetails() {
        Long studentId = studentService.getCurrentStudentId();
        return ResponseEntity.ok(ApiResponse.success(weeklyFinancialSummaryService.getRecentWeeklyReport(studentId, 0)));
    }

    @GetMapping("/growth")
    public ResponseEntity<ApiResponse<StudentFinancialSummaryDTO>> weeklyReportGrowthList() {
        StudentFinancialSummaryDTO summaryDTO = weeklyFinancialSummaryService.getStudentWeeklySummary();
        return ResponseEntity.ok(ApiResponse.success(summaryDTO));
    }

    /**
     * 📌 (학생) 최신 AI 피드백 조회 API
     */
    @GetMapping("/latest-ai-feedback")
    public ResponseEntity<ApiResponse<String>> getLatestAIFeedback() {
        Long studentId = studentService.getCurrentStudentId();
        String aiFeedback = weeklyFinancialSummaryService.getLatestAIFeedback(studentId);
        return ResponseEntity.ok(ApiResponse.success(aiFeedback));
    }

    @PostMapping("/predict")
    public Mono<ResponseEntity<ApiResponse<Map<String, Object>>>> predict(@RequestBody WeeklyFinancialSummaryDTO studentData) {
        return fastApiService.predictCluster(
                studentData.getTotalIncome(),
                studentData.getTotalExpenses(),
                studentData.getSavingsAmount(), // ✅ 총 투자 비용 (수정됨)
                studentData.getInvestmentReturn(),
                studentData.getTaxAmount(),
                studentData.getFineAmount(),
                studentData.getIncentiveAmount()
        ).map(ApiResponse::success).map(ResponseEntity::ok); // ✅ Mono를 비동기 방식으로 반환
    }

}

