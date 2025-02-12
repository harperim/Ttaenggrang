package com.ladysparks.ttaenggrang.domain.bank.controller;

import com.ladysparks.ttaenggrang.domain.bank.dto.SavingsDepositDTO;
import com.ladysparks.ttaenggrang.domain.bank.service.SavingsDepositService;
import com.ladysparks.ttaenggrang.global.docs.SavingsDepositApiSpecification;
import com.ladysparks.ttaenggrang.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/savings-deposits")
@RequiredArgsConstructor
public class SavingsDepositController implements SavingsDepositApiSpecification {

    private final SavingsDepositService savingsDepositService;

    // 미납된 적금 수동 납입
    @PostMapping("/{savingsDepositId}/retry")
    public ResponseEntity<ApiResponse<SavingsDepositDTO>> savingsDepositRetry(@PathVariable Long savingsDepositId) {
        SavingsDepositDTO savedDeposit = savingsDepositService.retrySavingsDeposit(savingsDepositId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(savedDeposit));
    }

    // 특정 적금 가입의 납입 내역 조회
    @GetMapping
    public ResponseEntity<ApiResponse<List<SavingsDepositDTO>>> savingsDepositList(@RequestParam Long savingsSubscriptionId) {
        List<SavingsDepositDTO> deposits = savingsDepositService.findSavingsDeposits(savingsSubscriptionId);
        return ResponseEntity.ok(ApiResponse.success(deposits));
    }

    // 미납 내역 조회
    @GetMapping("/fail")
    public ResponseEntity<ApiResponse<List<SavingsDepositDTO>>> savingsDepositsFailedList() {
        List<SavingsDepositDTO> failedDeposits = savingsDepositService.findFailedDeposits();
        return ResponseEntity.ok(ApiResponse.success(failedDeposits));
    }

}