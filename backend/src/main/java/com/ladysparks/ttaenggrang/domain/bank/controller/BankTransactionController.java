package com.ladysparks.ttaenggrang.domain.bank.controller;

import com.ladysparks.ttaenggrang.global.docs.BankTransactionApiSpecification;
import com.ladysparks.ttaenggrang.domain.bank.dto.BankTransactionDTO;
import com.ladysparks.ttaenggrang.global.response.ApiResponse;
import com.ladysparks.ttaenggrang.domain.bank.service.BankTransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/bank-transactions")
public class BankTransactionController implements BankTransactionApiSpecification {

    private final BankTransactionService bankTransactionService;

    // 은행 계좌 [등록]
    @PostMapping
    public ResponseEntity<ApiResponse<BankTransactionDTO>> bankTransactionAdd(@RequestBody @Valid BankTransactionDTO bankTransactionDTO) {
        BankTransactionDTO savedBankTransactionDTO = bankTransactionService.addBankTransaction(bankTransactionDTO);
        return ResponseEntity.ok(ApiResponse.success(savedBankTransactionDTO));
    }

    // 은행 계좌 거래 내역 [전체 조회]
    @GetMapping
    public ResponseEntity<ApiResponse<List<BankTransactionDTO>>> bankTransactionList() {
        List<BankTransactionDTO> bankTransactionDTOList = bankTransactionService.findBankTransactions();
        return ResponseEntity.ok(ApiResponse.success(bankTransactionDTOList));
    }

}
