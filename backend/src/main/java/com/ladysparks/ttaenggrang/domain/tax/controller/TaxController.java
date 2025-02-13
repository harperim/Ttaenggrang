package com.ladysparks.ttaenggrang.domain.tax.controller;

import com.ladysparks.ttaenggrang.domain.tax.dto.TaxDTO;
import com.ladysparks.ttaenggrang.domain.tax.service.TaxService;
import com.ladysparks.ttaenggrang.global.docs.TaxApiSpecification;
import com.ladysparks.ttaenggrang.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/taxes/")
@RequiredArgsConstructor
public class TaxController implements TaxApiSpecification {

    private final TaxService taxService;

    // 세금 항목 [등록]
    @PostMapping
    public ResponseEntity<ApiResponse<TaxDTO>> taxAdd(@RequestBody @Valid TaxDTO taxDTO) {
        ApiResponse<TaxDTO> response = taxService.addTax(taxDTO);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    // 세금 항목 [전체 조회]
    @GetMapping
    public ResponseEntity<ApiResponse<List<TaxDTO>>> taxList(@RequestParam Optional<Long> teacherId) {
        List<TaxDTO> taxDTOList = taxService.findTaxesByTeacher(teacherId);
        ApiResponse<List<TaxDTO>> response = ApiResponse.success(taxDTOList);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

}
