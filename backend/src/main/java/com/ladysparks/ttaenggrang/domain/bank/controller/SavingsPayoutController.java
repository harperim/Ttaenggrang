package com.ladysparks.ttaenggrang.domain.bank.controller;

import com.ladysparks.ttaenggrang.domain.bank.dto.SavingsPayoutDTO;
import com.ladysparks.ttaenggrang.domain.bank.service.SavingsPayoutService;
import com.ladysparks.ttaenggrang.domain.bank.service.SavingsSubscriptionService;
import com.ladysparks.ttaenggrang.domain.student.service.StudentService;
import com.ladysparks.ttaenggrang.global.docs.bank.SavingsPayoutApiSpecification;
import com.ladysparks.ttaenggrang.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/savings-payouts")
@RequiredArgsConstructor
public class SavingsPayoutController implements SavingsPayoutApiSpecification {

    private final SavingsPayoutService savingsPayoutService;
    private final StudentService studentService;
    private final SavingsSubscriptionService savingsSubscriptionService;

    @GetMapping
    public ResponseEntity<ApiResponse<SavingsPayoutDTO>> savingsPayoutMatured(@RequestParam Long savingsSubscriptionId) {
        return ResponseEntity.ok(ApiResponse.success(savingsPayoutService.getSavingsPayoutsBySubscriptionId(savingsSubscriptionId)));
    }

    @PatchMapping("/mature")
    public ResponseEntity<ApiResponse<SavingsPayoutDTO>> savingsPayoutPaid(@PathVariable Long savingsSubscriptionId) {
        Long studentId = studentService.getCurrentStudentId();
        String savingProductName = savingsSubscriptionService.getSavingsProductName(savingsSubscriptionId);
        return ResponseEntity.ok(ApiResponse.success(savingsPayoutService.receiveMaturityPayout(studentId, savingsSubscriptionId, savingProductName)));
    }

}