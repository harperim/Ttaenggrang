package com.ladysparks.ttaenggrang.domain.tax.service;

import com.ladysparks.ttaenggrang.domain.tax.dto.TaxInfoDTO;
import com.ladysparks.ttaenggrang.domain.tax.entity.TaxInfo;
import com.ladysparks.ttaenggrang.domain.tax.repository.TaxInfoRepository;
import com.ladysparks.ttaenggrang.domain.teacher.entity.Nation;
import com.ladysparks.ttaenggrang.domain.teacher.repository.NationRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaxInfoService {
    @Autowired
    private TaxInfoRepository taxInfoRepository;
    @Autowired
    private NationRepository nationRepository;

    @Transactional
    public List<TaxInfoDTO> useTax(Long nationId, TaxInfo taxInfo) {
        // 국고 정보 가져오기
        Nation nation = nationRepository.findById(nationId)
                .orElseThrow(() -> new IllegalArgumentException("해당 국가 정보가 없습니다."));

        // 국고 금액보다 사용 금액이 크면 예외 발생
        if (nation.getNationalTreasury() < taxInfo.getTaxAmt()) {
            throw new IllegalArgumentException("국고에 충분한 금액이 없습니다.");
        }

        // 국고에서 차감
        nation.setNationalTreasury(nation.getNationalTreasury() - taxInfo.getTaxAmt());
        nationRepository.save(nation);

        // 세금 사용 내역 저장
        TaxInfo savedTaxInfo = taxInfoRepository.save(taxInfo);

        // DTO 변환 및 반환
        return taxInfoRepository.findAll().stream()
                .map(TaxInfoDTO::fromEntity)
                .collect(Collectors.toList());
    }
}
