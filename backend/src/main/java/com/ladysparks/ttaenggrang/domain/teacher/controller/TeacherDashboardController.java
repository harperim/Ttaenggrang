package com.ladysparks.ttaenggrang.domain.teacher.controller;

import com.ladysparks.ttaenggrang.domain.bank.dto.StudentDailyAverageFinancialDTO;
import com.ladysparks.ttaenggrang.domain.bank.service.BankTransactionService;
import com.ladysparks.ttaenggrang.domain.teacher.dto.StudentManagementDTO;
import com.ladysparks.ttaenggrang.domain.teacher.dto.TeacherDashboardDTO;
import com.ladysparks.ttaenggrang.domain.teacher.service.TeacherDashboardService;
import com.ladysparks.ttaenggrang.domain.teacher.service.TeacherService;
import com.ladysparks.ttaenggrang.global.docs.home.TeacherDashboardApiSpecification;
import com.ladysparks.ttaenggrang.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/teachers/{teacherId}/dashboard")
public class TeacherDashboardController implements TeacherDashboardApiSpecification {

    private final TeacherDashboardService teacherDashboardService;
    private final BankTransactionService bankTransactionService;
    private final TeacherService teacherService;

    @GetMapping("/daily-average")
    public ResponseEntity<ApiResponse<List<StudentDailyAverageFinancialDTO>>> dailyAverageIncomeAndExpenseDetails() {
        List<StudentDailyAverageFinancialDTO> response = bankTransactionService.getDailyAverageIncomeAndExpense();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<TeacherDashboardDTO>> teacherDashboardDetails() {
        TeacherDashboardDTO teacherDashboardDTO = teacherDashboardService.getTeacherDashboardData();
        return ResponseEntity.ok(ApiResponse.success(teacherDashboardDTO));
    }

    @GetMapping("/student-management")
    public ResponseEntity<ApiResponse<List<StudentManagementDTO>>> studentManagementDetails() {
        Long teacherId = teacherService.getCurrentTeacherId();
        List<StudentManagementDTO> response = teacherDashboardService.getStudentManagementListByTeacher(teacherId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

}
