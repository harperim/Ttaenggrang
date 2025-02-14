package com.ladysparks.ttaenggrang.domain.bank.controller;

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

    @Autowired
    public SavingsProductController(SavingsProductService savingsProductService) {
        this.savingsProductService = savingsProductService;
    }

    // 적금 상품 [등록]
    @PostMapping
    public ResponseEntity<ApiResponse<SavingsProductDTO>> savingsProductAdd(@RequestBody @Valid SavingsProductDTO savingsProductDTO) {
        SavingsProductDTO savedSavingProductDTO = savingsProductService.addSavingsProducts(savingsProductDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(savedSavingProductDTO)); // 201 Created
    }

    // 적금 상품 [조회]
    @GetMapping
    public ResponseEntity<ApiResponse<List<SavingsProductDTO>>> savingsProductList() {
        List<SavingsProductDTO> savingsProductDTOList = savingsProductService.findSavingsProducts();
        return ResponseEntity.ok(ApiResponse.success(savingsProductDTOList));
    }

}
