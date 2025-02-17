package com.ladysparks.ttaenggrang.domain.tax.controller;

import com.ladysparks.ttaenggrang.domain.tax.dto.TaxUsageDTO;
import com.ladysparks.ttaenggrang.domain.tax.service.TaxUsageService;
import com.ladysparks.ttaenggrang.domain.teacher.service.NationService;
import com.ladysparks.ttaenggrang.domain.teacher.service.TeacherService;
import com.ladysparks.ttaenggrang.global.docs.tax.TaxUsageApiSpecification;
import com.ladysparks.ttaenggrang.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.webjars.NotFoundException;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/taxes/")
public class TaxUsageController implements TaxUsageApiSpecification {

    private final TeacherService teacherService;
    private final NationService nationService;
    private final TaxUsageService taxUsageService;

    @PostMapping("/use")
    public ResponseEntity<ApiResponse<TaxUsageDTO>> taxUsage(@RequestBody TaxUsageDTO taxUsageDTO) {
        Long teacherId = teacherService.getCurrentTeacherId();
        return ResponseEntity.ok(ApiResponse.success(taxUsageService.useTax(teacherId, taxUsageDTO)));
    }

    @GetMapping("/use")
    public ResponseEntity<ApiResponse<List<TaxUsageDTO>>> taxUsageList() {
        Long teacherId = teacherService.getCurrentTeacherId();
        Long nationId = nationService.findNationDTOByTeacherId(teacherId)
                .orElseThrow(() -> new NotFoundException("등록된 국가가 없습니다."))
                .getId();
        return ResponseEntity.ok(ApiResponse.success(taxUsageService.getTaxUsageByNationId(nationId)));
    }

}
