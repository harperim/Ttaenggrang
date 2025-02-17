package com.ladysparks.ttaenggrang.domain.weekly_report.controller;

import com.ladysparks.ttaenggrang.domain.student.service.StudentService;
import com.ladysparks.ttaenggrang.domain.weekly_report.dto.StudentFinancialSummaryDTO;
import com.ladysparks.ttaenggrang.domain.weekly_report.dto.WeeklyFinancialSummaryDTO;
import com.ladysparks.ttaenggrang.domain.weekly_report.dto.WeeklyReportDTO;
import com.ladysparks.ttaenggrang.domain.weekly_report.service.WeeklyFinancialSummaryService;
import com.ladysparks.ttaenggrang.domain.weekly_report.service.WeeklyReportService;
import com.ladysparks.ttaenggrang.global.docs.weekly.WeeklyReportApiSpecification;
import com.ladysparks.ttaenggrang.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/weekly-report")
public class WeeklyReportController implements WeeklyReportApiSpecification {

    private final WeeklyReportService weeklyReportService;
    private final WeeklyFinancialSummaryService weeklyFinancialSummaryService;
    private final StudentService studentService;

    @GetMapping
    public ResponseEntity<ApiResponse<WeeklyFinancialSummaryDTO>> weeklyReportDetails() {
        return ResponseEntity.ok(ApiResponse.success(weeklyFinancialSummaryService.getThisWeekReport()));
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

}

