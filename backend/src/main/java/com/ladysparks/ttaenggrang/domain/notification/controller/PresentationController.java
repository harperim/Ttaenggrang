package com.ladysparks.ttaenggrang.domain.notification.controller;

import com.ladysparks.ttaenggrang.domain.notification.service.NotificationService;
import com.ladysparks.ttaenggrang.domain.teacher.service.TeacherService;
import com.ladysparks.ttaenggrang.domain.weekly_report.service.WeeklyFinancialSummaryService;
import com.ladysparks.ttaenggrang.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Tag(name = "시연용 API")
@RestController
@RequiredArgsConstructor
@CrossOrigin("*")
@RequestMapping("/admin")
public class PresentationController {

    private final TeacherService teacherService;
    private final NotificationService notificationService;
    private final WeeklyFinancialSummaryService weeklyFinancialSummaryService;

    /**
     * 뉴스 발행 FCM 알림
     */
    @Operation(summary = "뉴스 발행 FCM 알림")
    @GetMapping("/news")
    public ResponseEntity<ApiResponse<Boolean>> newsFCM() throws IOException {
        Long teacherId = teacherService.getCurrentTeacherId();
        // 학생들에게 FCM 알림 전송
        notificationService.sendNewsNotificationToStudents(teacherId, "");
        return ResponseEntity.ok(ApiResponse.success(true));
    }

    /**
     * 적금 만기 FCM 알림
     */
    @Operation(summary = "적금 만기 FCM 알림")
    @GetMapping("/savings-maturity")
    public ResponseEntity<ApiResponse<Boolean>> bankFCM() throws IOException {
        Long teacherId = teacherService.getCurrentTeacherId();
        // 학생들에게 FCM 알림 전송
        notificationService.sendBankNotificationToStudents(teacherId, "");
        return ResponseEntity.ok(ApiResponse.success(true));
    }

    /**
     * 주간 리포트 발행 FCM 알림
     */
    @Operation(summary = "주간 리포트 발행 FCM 알림")
    @GetMapping("/weekly-report")
    public ResponseEntity<ApiResponse<Boolean>> weeklyReportFCM(@RequestParam Long studentId) throws IOException {
        // 주간 리포트 생성
        weeklyFinancialSummaryService.generateWeeklyReports(studentId);

        // 학생에게 FCM 알림 전송
        notificationService.sendWeeeklyNotificationToStudents(studentId);

        return ResponseEntity.ok(ApiResponse.success(true));
    }

}
