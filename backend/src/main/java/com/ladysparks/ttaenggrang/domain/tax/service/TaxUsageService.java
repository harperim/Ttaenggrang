package com.ladysparks.ttaenggrang.domain.tax.service;

import com.ladysparks.ttaenggrang.domain.tax.dto.TaxUsageDTO;
import com.ladysparks.ttaenggrang.domain.tax.entity.TaxUsage;
import com.ladysparks.ttaenggrang.domain.tax.repository.TaxUsageRepository;
import com.ladysparks.ttaenggrang.domain.teacher.entity.Nation;
import com.ladysparks.ttaenggrang.domain.teacher.service.NationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaxUsageService {

    private final TaxUsageRepository taxUsageRepository;
    private final NationService nationService;

    @Transactional
    public TaxUsageDTO useTax(Long teacherId, TaxUsageDTO taxUsageDTO) {
        // 국고 가져오기
        Nation nation = nationService.findNationByTeacherId(teacherId)
                .orElseThrow(() -> new IllegalArgumentException("해당 국가 정보가 없습니다."));

        // 국고 금액보다 사용 금액이 크면 예외 발생
        int nationalTreasuryBalance = nation.getNationalTreasury() - taxUsageDTO.getAmount();
        if (nationalTreasuryBalance < 0) {
            throw new IllegalArgumentException("국고에 충분한 금액이 없습니다.");
        }

        // 국고에서 차감
        nationService.updateNationTreasury(nation.getId(), nationalTreasuryBalance);

        // 세금 사용 내역 저장 (국가 정보 포함)
        TaxUsage taxUsage = TaxUsage.builder()
                .name(taxUsageDTO.getName())
                .amount(taxUsageDTO.getAmount())
                .description(taxUsageDTO.getDescription())
                .usageDate(taxUsageDTO.getUsageDate())
                .nation(nation) // Nation 정보 추가
                .build();

        TaxUsage savedTaxUsage = taxUsageRepository.save(taxUsage);

        // DTO 변환 및 반환
        return TaxUsageDTO.toDto(savedTaxUsage);
    }

    public List<TaxUsageDTO> getTaxUsageByNationId(Long nationId) {
        List<TaxUsage> taxUsages = taxUsageRepository.findByNationId(nationId);
        return taxUsages.stream()
                .map(TaxUsageDTO::toDto)
                .collect(Collectors.toList());
    }

}
