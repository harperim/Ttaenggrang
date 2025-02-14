package com.ladysparks.ttaenggrang.global.docs.tax;

import com.ladysparks.ttaenggrang.domain.tax.dto.TaxInfoDTO;
import com.ladysparks.ttaenggrang.domain.tax.entity.TaxInfo;
import com.ladysparks.ttaenggrang.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Tag(name = "[교사] 국세청 국고 사용", description = "세금 관련 API")
public interface TaxInfoApiSpecification {
    @Operation(summary = "국고 사용", description = "💡 국고 사용합니다.")
    @PostMapping("/{nationId}/use")
    public ResponseEntity<ApiResponse<List<TaxInfoDTO>>> useTax(
            @PathVariable Long nationId,
            @RequestBody TaxInfo taxInfo);
}
