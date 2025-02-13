package com.ladysparks.ttaenggrang.domain.bank.controller;

import com.ladysparks.ttaenggrang.domain.bank.dto.SavingsGoalDTO;
import com.ladysparks.ttaenggrang.global.response.ApiResponse;
import com.ladysparks.ttaenggrang.domain.bank.service.SavingsGoalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/savings-goals")
public class SavingGoalController {

    private final SavingsGoalService savingsGoalService;

    @Autowired
    public SavingGoalController(SavingsGoalService savingsGoalService) {
        this.savingsGoalService = savingsGoalService;
    }

    // 저축 목표 [등록]
    @PostMapping
    public ResponseEntity<ApiResponse<SavingsGoalDTO>> savingGoalAdd(@RequestBody SavingsGoalDTO savingsGoalDTO) {
        SavingsGoalDTO savedSavingsGoalDTO = savingsGoalService.addSavingsGoal(savingsGoalDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(savedSavingsGoalDTO));
    }

    // 저축 목표 [전체 조회]
    @GetMapping
    public ResponseEntity<ApiResponse<List<SavingsGoalDTO>>> savingGoalList(@RequestParam Long studentId) {
        List<SavingsGoalDTO> savingsGoalDTOList = savingsGoalService.findSavingsGoals(studentId);
        return ResponseEntity.ok(ApiResponse.success(savingsGoalDTOList));
    }

}
