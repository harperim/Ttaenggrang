package com.ladysparks.ttaenggrang.domain.student.controller;

import com.ladysparks.ttaenggrang.domain.student.dto.SavingsAchievementDTO;
import com.ladysparks.ttaenggrang.domain.student.service.StudentService;
import com.ladysparks.ttaenggrang.global.docs.StudentDashboardApiSpecification;
import com.ladysparks.ttaenggrang.global.redis.RedisGoalService;
import com.ladysparks.ttaenggrang.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/students/{studentId}/savings-achievement")
@RequiredArgsConstructor
public class StudentDashboardController implements StudentDashboardApiSpecification {

    private final StudentService studentService;
    private final RedisGoalService redisGoalService;

    @GetMapping
    public ResponseEntity<ApiResponse<SavingsAchievementDTO>> SavingsAchievementRate() {
        SavingsAchievementDTO savingsAchievementDTO = studentService.getSavingsAchievementRate();
        return ResponseEntity.ok(ApiResponse.success(savingsAchievementDTO));
    }

    /**
     * 목표 달성률 기준 TOP N 학생 조회
     */
    @GetMapping("/top")
    public ResponseEntity<ApiResponse<List<SavingsAchievementDTO>>> TopStudentList(@RequestParam int topN) {
        Long studentId = studentService.getCurrentStudentId();
        Long teacherId = studentService.findTeacherIdByStudentId(studentId);
        studentService.getAllSavingsAchievementRates(studentId, teacherId);
        List<SavingsAchievementDTO> topStudents = redisGoalService.getTopStudents(teacherId, topN);
        return ResponseEntity.ok(ApiResponse.success(topStudents));
    }

}
