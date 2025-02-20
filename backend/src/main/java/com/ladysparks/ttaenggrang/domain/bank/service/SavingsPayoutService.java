package com.ladysparks.ttaenggrang.domain.bank.service;

import com.ladysparks.ttaenggrang.domain.bank.dto.BankTransactionDTO;
import com.ladysparks.ttaenggrang.domain.bank.dto.SavingsPayoutDTO;
import com.ladysparks.ttaenggrang.domain.bank.dto.SavingsSubscriptionDTO;
import com.ladysparks.ttaenggrang.domain.bank.entity.BankTransaction.BankTransactionType;
import com.ladysparks.ttaenggrang.domain.bank.entity.SavingsPayout;
import com.ladysparks.ttaenggrang.domain.bank.entity.SavingsSubscription;
import com.ladysparks.ttaenggrang.domain.bank.mapper.SavingsPayoutMapper;
import com.ladysparks.ttaenggrang.domain.bank.repository.SavingsPayoutRepository;
import com.ladysparks.ttaenggrang.domain.bank.repository.SavingsSubscriptionRepository;
import com.ladysparks.ttaenggrang.domain.student.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SavingsPayoutService {

    private final SavingsPayoutRepository savingsPayoutRepository;
    private final SavingsPayoutMapper savingsPayoutMapper;
    private final StudentService studentService;
    private final BankTransactionService bankTransactionService;
    private final SavingsSubscriptionRepository savingsSubscriptionRepository;

    /**
     * 지금 내역 확인
     * 지급 내역이 생성되면 확인할 수 있음
     */
    @Transactional(readOnly = true)
    public SavingsPayoutDTO getSavingsPayoutsBySubscriptionId(Long savingsSubscriptionId) {
        return savingsPayoutMapper.toDto(savingsPayoutRepository.findBySavingsSubscriptionId(savingsSubscriptionId));
    }

    /**
     * 적급 지금 내역 생성
     */
    @Transactional
    public SavingsPayoutDTO createPayout(Long subscriptionId) {
        // 1. 적금 구독 정보 가져오기
        SavingsSubscription subscription = savingsSubscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new IllegalArgumentException("적금 가입 정보를 찾을 수 없습니다."));

        // 2. 이자 계산. 지급 총액 계산
        int principal = subscription.getDepositAmount();
        int payoutAmount = subscription.getSavingsProduct().getPayoutAmount();
        int interestAmount = payoutAmount - principal;

        // 3. 지급 내역 생성
        return SavingsPayoutDTO.builder()
                .savingsSubscriptionId(subscription.getId())
                .payoutAmount(payoutAmount)
                .interestAmount(interestAmount)
                .paid(true)
                .payoutDate(LocalDate.now()) // 오늘 날짜 지급
                .payoutType(SavingsPayout.SavingsPayoutType.MATURITY)
                .build();
    }

    /**
     * 만기 지금 받기
     */
    @Transactional
    public SavingsPayoutDTO receiveMaturityPayout(Long studentId, Long savingsSubscriptionId, String savingProductName) {
        // 1. 지급 내역 생성
        SavingsPayoutDTO savingsPayoutDTO = createPayout(savingsSubscriptionId);

        // 2. 지급 내역 저장
        SavingsPayout savedSavingsPayout = savingsPayoutRepository.save(savingsPayoutMapper.toEntity(savingsPayoutDTO));

        // 3. 지급 금액 입금
        Long bankAccountId = studentService.findBankAccountIdById(studentId);
        BankTransactionDTO bankTransactionDTO = BankTransactionDTO.builder()
                .bankAccountId(bankAccountId)
                .type(BankTransactionType.SAVINGS_INTEREST)
                .amount(savedSavingsPayout.getPayoutAmount())
                .description("[적금 만기] 상품명: " + savingProductName)
                .receiverId(null)
                .build();

        bankTransactionService.addBankTransaction(bankTransactionDTO);

        // 4. DTO 변환 후 반환
        return savingsPayoutMapper.toDto(savedSavingsPayout);
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
