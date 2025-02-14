package com.ladysparks.ttaenggrang.domain.bank.controller;

import com.ladysparks.ttaenggrang.domain.student.service.StudentService;
import com.ladysparks.ttaenggrang.global.docs.bank.BankAccountApiSpecification;
import com.ladysparks.ttaenggrang.domain.bank.dto.BankAccountDTO;
import com.ladysparks.ttaenggrang.global.response.ApiResponse;
import com.ladysparks.ttaenggrang.domain.bank.service.BankAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/bank-accounts")
public class BankAccountController implements BankAccountApiSpecification {

    private final BankAccountService bankAccountService;
    private final StudentService studentService;

    // 은행 계좌 [등록]
    /*
    @PostMapping
    public ResponseEntity<ApiResponse<BankAccountDTO>> BankAccountAdd(@RequestBody BankAccountDTO bankAccountDto) {
        BankAccountDTO savedBankAccountDto = bankAccountService.addBankAccount(bankAccountDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(savedBankAccountDto));
    }
    */

    // 은행 계좌 [조회]
    @GetMapping
    public ResponseEntity<ApiResponse<BankAccountDTO>> BankAccountDetails() {
        Long studentId = studentService.getCurrentStudentId();
        Long bankAccountId = studentService.findBankAccountIdById(studentId);
        BankAccountDTO bankAccountDTO = bankAccountService.findBankAccount(bankAccountId);
        return ResponseEntity.ok(ApiResponse.success(bankAccountDTO));
    }

}
