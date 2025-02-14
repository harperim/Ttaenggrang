package com.ladysparks.ttaenggrang.domain.tax.controller;

import com.ladysparks.ttaenggrang.domain.tax.dto.TaxInfoDTO;
import com.ladysparks.ttaenggrang.domain.tax.entity.TaxInfo;
import com.ladysparks.ttaenggrang.domain.tax.service.TaxInfoService;
import com.ladysparks.ttaenggrang.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/taxes/")
@RequiredArgsConstructor
public class TaxInfoController {
    @Autowired
    private TaxInfoService taxInfoService;

    @PostMapping("/{nationId}/use")
    public ResponseEntity<ApiResponse<List<TaxInfoDTO>>> useTax(
            @PathVariable Long nationId,
            @RequestBody TaxInfo taxInfo)
    {

        return ResponseEntity.ok(ApiResponse.success(taxInfoService.useTax(nationId, taxInfo)));
    }

}
