package com.ladysparks.ttaenggrang.domain.student.controller;

import com.ladysparks.ttaenggrang.domain.student.dto.BankTransactionSummaryDTO;
import com.ladysparks.ttaenggrang.domain.student.dto.StudentAssetDTO;
import com.ladysparks.ttaenggrang.domain.student.dto.StudentDashboardDTO;
import com.ladysparks.ttaenggrang.domain.student.service.StudentDashboardService;
import com.ladysparks.ttaenggrang.domain.student.service.StudentService;
import com.ladysparks.ttaenggrang.global.docs.home.StudentDashboardApiSpecification;
import com.ladysparks.ttaenggrang.global.redis.RedisGoalService;
import com.ladysparks.ttaenggrang.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/students/{studentId}/dashboard")
@RequiredArgsConstructor
public class StudentDashboardController implements StudentDashboardApiSpecification {

    private final StudentService studentService;
    private final StudentDashboardService studentDashboardService;
    private final RedisGoalService redisGoalService;

    @GetMapping
    public ResponseEntity<ApiResponse<StudentDashboardDTO>> studentDashboardDetails() {
        Long studentId = studentService.getCurrentStudentId();
        StudentDashboardDTO studentDashboardDTO = studentDashboardService.getStudentDashboard(studentId);
        return ResponseEntity.ok(ApiResponse.success(studentDashboardDTO));
    }

    @GetMapping("/asset")
    public ResponseEntity<ApiResponse<StudentAssetDTO>> studentDashboardAsset() {
        Long studentId = studentService.getCurrentStudentId();
        StudentAssetDTO studentAssetDTO = studentDashboardService.getStudentAsset(studentId);
        return ResponseEntity.ok(ApiResponse.success(studentAssetDTO));
    }

    @GetMapping("/bank-transactions")
    public ResponseEntity<ApiResponse<List<BankTransactionSummaryDTO>>> getStudentTransactionSummaryList() {
        Long studentId = studentService.getCurrentStudentId();
        List<BankTransactionSummaryDTO> bankTransactionSummaryDTOList = studentDashboardService.getStudentTransactionSummaryList(studentId);
        return ResponseEntity.ok(ApiResponse.success(bankTransactionSummaryDTOList));
    }

    /**
     * 목표 달성률 기준 TOP N 학생 조회
     */
//    @GetMapping("/top")
//    public ResponseEntity<ApiResponse<List<SavingsAchievementDTO>>> TopStudentList(@RequestParam int topN) {
//        Long studentId = studentService.getCurrentStudentId();
//        Long teacherId = studentService.findTeacherIdByStudentId(studentId);
//        studentService.getAllSavingsAchievementRates(studentId, teacherId);
//        List<SavingsAchievementDTO> topStudents = redisGoalService.getTopStudents(teacherId, topN);
//        return ResponseEntity.ok(ApiResponse.success(topStudents));
//    }

}
