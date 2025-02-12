package com.ladysparks.ttaenggrang.domain.bank.service;

import com.ladysparks.ttaenggrang.domain.bank.dto.SavingsPayoutDTO;
import com.ladysparks.ttaenggrang.domain.bank.dto.SavingsSubscriptionDTO;
import com.ladysparks.ttaenggrang.domain.bank.entity.SavingsPayout;
import com.ladysparks.ttaenggrang.domain.bank.mapper.SavingsPayoutMapper;
import com.ladysparks.ttaenggrang.domain.bank.repository.SavingsPayoutRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SavingsPayoutService {

    private final SavingsPayoutRepository savingsPayoutRepository;
    private final SavingsPayoutMapper savingsPayoutMapper;

    public SavingsPayoutDTO createPayout(SavingsPayoutDTO dto) {
        SavingsPayout savingsPayout = savingsPayoutMapper.toEntity(dto);
        savingsPayout = savingsPayoutRepository.save(savingsPayout);
        return savingsPayoutMapper.toDto(savingsPayout);
    }

    @Transactional(readOnly = true)
    public SavingsPayoutDTO getSavingsPayoutsBySubscriptionId(Long savingsSubscriptionId) {
        return savingsPayoutMapper.toDto(savingsPayoutRepository.findBySavingsSubscriptionId(savingsSubscriptionId));
    }

    public int getTotalPayoutAmount(List<SavingsSubscriptionDTO> savingsSubscriptionDTOList) {
        return savingsSubscriptionDTOList.stream()
                .mapToInt(savingsSubscriptionDTO -> getSavingsPayoutsBySubscriptionId(savingsSubscriptionDTO.getId()).getPayoutAmount())
                .sum();
    }

}
