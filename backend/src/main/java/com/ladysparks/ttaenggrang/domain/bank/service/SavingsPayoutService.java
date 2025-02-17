package com.ladysparks.ttaenggrang.domain.bank.service;

import com.ladysparks.ttaenggrang.domain.bank.dto.BankTransactionDTO;
import com.ladysparks.ttaenggrang.domain.bank.dto.SavingsPayoutDTO;
import com.ladysparks.ttaenggrang.domain.bank.dto.SavingsSubscriptionDTO;
import com.ladysparks.ttaenggrang.domain.bank.entity.BankTransaction.BankTransactionType;
import com.ladysparks.ttaenggrang.domain.bank.entity.SavingsPayout;
import com.ladysparks.ttaenggrang.domain.bank.mapper.SavingsPayoutMapper;
import com.ladysparks.ttaenggrang.domain.bank.repository.SavingsPayoutRepository;
import com.ladysparks.ttaenggrang.domain.student.service.StudentService;
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
    private final StudentService studentService;
    private final BankTransactionService bankTransactionService;

    /**
     * 지금 내역 확인
     * 지급 내역이 생성되면 확인할 수 있음
     */
    @Transactional(readOnly = true)
    public SavingsPayoutDTO getSavingsPayoutsBySubscriptionId(Long savingsSubscriptionId) {
        return savingsPayoutMapper.toDto(savingsPayoutRepository.findBySavingsSubscriptionId(savingsSubscriptionId));
    }

    /**
     * 만기 지금 받기
     */
    @Transactional
    public SavingsPayoutDTO receiveMaturityPayout(Long stuedentId, Long savingsSubscriptionId, String savingProductName) {
        // 1. 지급 내역 조회
        SavingsPayout savingsPayout = savingsPayoutRepository.findBySavingsSubscriptionId(savingsSubscriptionId);

        // 2. 이미 지급 완료된 경우 예외 처리
        if (savingsPayout.isPaid()) {
            throw new IllegalStateException("이미 지급 완료된 만기 금액입니다.");
        }

        // 3. 지급 상태 변경
        savingsPayout.setPaid(true);
        SavingsPayout savedSavingsPayout = savingsPayoutRepository.save(savingsPayout);

        // 4. 지급 금액 입금
        Long bankAccountId = studentService.findBankAccountIdById(stuedentId);
        BankTransactionDTO bankTransactionDTO = BankTransactionDTO.builder()
                .bankAccountId(bankAccountId)
                .type(BankTransactionType.SAVINGS_PAYOUT)
                .amount(savingsPayout.getPayoutAmount())
                .description("[적금 만기] 상품명: " + savingProductName)
                .receiverId(null)
                .build();

        bankTransactionService.addBankTransaction(bankTransactionDTO);

        // 5. DTO 변환 후 반환
        return savingsPayoutMapper.toDto(savedSavingsPayout);
    }

    /**
     * 지급 내역 생성
     * 만기일이 되고 납입 조건을 만족하면 지급 내역이 생성됨
     */
    public SavingsPayoutDTO createPayout(SavingsPayoutDTO dto) {
        SavingsPayout savingsPayout = savingsPayoutMapper.toEntity(dto);
        savingsPayout = savingsPayoutRepository.save(savingsPayout);
        return savingsPayoutMapper.toDto(savingsPayout);
    }

    /**
     * 적금에서 총 지급된 금액 계산
     */
    public int getTotalPayoutAmount(List<SavingsSubscriptionDTO> savingsSubscriptionDTOList) {
        return savingsSubscriptionDTOList.stream()
                .mapToInt(savingsSubscriptionDTO -> getSavingsPayoutsBySubscriptionId(savingsSubscriptionDTO.getId()).getPayoutAmount())
                .sum();
    }

}
