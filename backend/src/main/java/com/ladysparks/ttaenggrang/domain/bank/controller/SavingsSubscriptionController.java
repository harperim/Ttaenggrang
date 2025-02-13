package com.ladysparks.ttaenggrang.domain.bank.controller;

import com.ladysparks.ttaenggrang.domain.student.service.StudentService;
import com.ladysparks.ttaenggrang.global.docs.SavingsSubscriptionApiSpecification;
import com.ladysparks.ttaenggrang.domain.bank.dto.SavingsSubscriptionDTO;
import com.ladysparks.ttaenggrang.global.response.ApiResponse;
import com.ladysparks.ttaenggrang.domain.bank.service.SavingsSubscriptionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/savings-subscriptions")
public class SavingsSubscriptionController implements SavingsSubscriptionApiSpecification {

    private final SavingsSubscriptionService savingsSubscriptionService;
    private final StudentService studentService;

    @Autowired
    public SavingsSubscriptionController(SavingsSubscriptionService savingsSubscriptionService, StudentService studentService) {
        this.savingsSubscriptionService = savingsSubscriptionService;
        this.studentService = studentService;
    }

    // 적금 가입 [등록]
    @PostMapping
    public ResponseEntity<ApiResponse<SavingsSubscriptionDTO>> savingsSubscriptionAdd(@RequestBody @Valid SavingsSubscriptionDTO savingsSubscriptionDTO) {
        SavingsSubscriptionDTO savesSavingsSubscriptionDTO = savingsSubscriptionService.addSavingsSubscription(savingsSubscriptionDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(savesSavingsSubscriptionDTO));
    }

    // 적금 가입 (학생) 내역 [전체 조회]
    @GetMapping
    public ResponseEntity<ApiResponse<List<SavingsSubscriptionDTO>>> savingsSubscriptionList() {
        Long studentId = studentService.getCurrentStudentId();
        return ResponseEntity.ok(ApiResponse.success(savingsSubscriptionService.findSavingsSubscriptionsByStudentId(studentId)));
    }

}
