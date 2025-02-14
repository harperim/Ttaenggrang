package com.ladysparks.ttaenggrang.domain.bank.controller;

import com.ladysparks.ttaenggrang.domain.bank.dto.SavingsPayoutDTO;
import com.ladysparks.ttaenggrang.domain.bank.service.SavingsPayoutService;
import com.ladysparks.ttaenggrang.global.docs.bank.SavingsPayoutApiSpecification;
import com.ladysparks.ttaenggrang.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/savings-payouts")
@RequiredArgsConstructor
public class SavingsPayoutController implements SavingsPayoutApiSpecification {

    private final SavingsPayoutService savingsPayoutService;

    @GetMapping
    public ResponseEntity<ApiResponse<SavingsPayoutDTO>> savingsPayoutDetails(@RequestParam Long savingsSubscriptionId) {
        return ResponseEntity.ok(ApiResponse.success(savingsPayoutService.getSavingsPayoutsBySubscriptionId(savingsSubscriptionId)));
    }

}