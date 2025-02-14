package com.ladysparks.ttaenggrang.domain.bank.controller;

import com.ladysparks.ttaenggrang.domain.bank.dto.DepositAndSavingsCountDTO;
import com.ladysparks.ttaenggrang.domain.teacher.service.TeacherService;
import com.ladysparks.ttaenggrang.global.docs.bank.SavingsProductApiSpecification;
import com.ladysparks.ttaenggrang.domain.bank.dto.SavingsProductDTO;
import com.ladysparks.ttaenggrang.global.response.ApiResponse;
import com.ladysparks.ttaenggrang.domain.bank.service.SavingsProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/savings-products")
public class SavingsProductController implements SavingsProductApiSpecification {

    private final SavingsProductService savingsProductService;
    private final TeacherService teacherService;

    @Autowired
    public SavingsProductController(SavingsProductService savingsProductService, TeacherService teacherService) {
        this.savingsProductService = savingsProductService;
        this.teacherService = teacherService;
    }

    // (교사) 적금 상품 [등록]
    @PostMapping
    public ResponseEntity<ApiResponse<SavingsProductDTO>> savingsProductAdd(@RequestBody @Valid SavingsProductDTO savingsProductDTO) {
        SavingsProductDTO savedSavingProductDTO = savingsProductService.addSavingsProducts(savingsProductDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(savedSavingProductDTO));
    }

    // 적금 상품 [조회]
    @GetMapping
    public ResponseEntity<ApiResponse<List<SavingsProductDTO>>> savingsProductList() {
        List<SavingsProductDTO> savingsProductDTOList = savingsProductService.findSavingsProducts();
        return ResponseEntity.ok(ApiResponse.success(savingsProductDTOList));
    }

    @GetMapping("/counts")
    public ResponseEntity<ApiResponse<DepositAndSavingsCountDTO>> depositAndSavingsCounts() {
        Long teacherId = teacherService.getCurrentTeacherId();
        DepositAndSavingsCountDTO result = savingsProductService.getDepositAndSavingsCountsByTeacherId(teacherId);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

}
