package com.ladysparks.ttaenggrang.domain.bank.service;

import com.ladysparks.ttaenggrang.domain.bank.dto.*;
import com.ladysparks.ttaenggrang.domain.bank.entity.BankTransaction.BankTransactionType;
import com.ladysparks.ttaenggrang.domain.bank.entity.SavingsDeposit;
import com.ladysparks.ttaenggrang.domain.bank.entity.SavingsDeposit.SavingsDepositStatus;
import com.ladysparks.ttaenggrang.domain.bank.entity.SavingsSubscription;
import com.ladysparks.ttaenggrang.domain.bank.mapper.SavingsDepositMapper;
import com.ladysparks.ttaenggrang.domain.bank.repository.SavingsDepositRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SavingsDepositService {

    private final SavingsDepositRepository savingsDepositRepository;
    private final SavingsDepositMapper savingsDepositMapper;
    private final BankAccountService bankAccountService;
    private final BankTransactionService bankTransactionService;

    // 적금 납입 내역 조회 (특정 적금 가입)
    public List<SavingsDepositDTO> findSavingsDeposits(Long savingsSubscriptionId) {
        List<SavingsDeposit> deposits = savingsDepositRepository.findBySavingsSubscriptionId(savingsSubscriptionId);
        return deposits.stream()
                .map(savingsDepositMapper::toDto)
                .collect(Collectors.toList());
    }

    // 적금 납입 내역 추가
    @Transactional
    public SavingsDepositDTO addSavingsDeposit(SavingsDepositDTO savingsDepositDTO) {
        // 적금 납입 내역 저장
        SavingsDeposit savingsDeposit = savingsDepositMapper.toEntity(savingsDepositDTO);
        SavingsDeposit savedDeposit = savingsDepositRepository.save(savingsDeposit);

        return savingsDepositMapper.toDto(savedDeposit);
    }

    // 적금 납입 내역 추가 (수동 납입)
    @Transactional
    public SavingsDepositDTO retrySavingsDeposit(Long savingsDepositId, Long bankAccountId) {
        SavingsDeposit savingsDeposit = savingsDepositRepository.findById(savingsDepositId)
                .orElseThrow(() -> new IllegalArgumentException("해당 적금 납입 정보를 찾을 수 없습니다."));

        BankAccountDTO bankAccountDTO = bankAccountService.findBankAccount(bankAccountId);

        int amount = savingsDeposit.getSavingsSubscription().getSavingsProduct().getAmount();

        if (bankAccountDTO.getBalance() < amount) {
            throw new IllegalArgumentException("현재 은행 계좌 잔액이 부족합니다. (현재 잔액: " + bankAccountDTO.getBalance() + ")");
        }

        // 미납된 적금 납입 처리
        BankTransactionDTO bankTransactionDTO = BankTransactionDTO.builder()
                .bankAccountId(bankAccountDTO.getId())
                .type(BankTransactionType.SAVINGS_DEPOSIT)
                .amount(amount)
                .description("[적금 납입] 적금 상품명: " + savingsDeposit.getSavingsSubscription().getSavingsProduct().getName())
                .build();
        bankTransactionService.addBankTransaction(bankTransactionDTO);

        savingsDeposit.updateAmount(amount);
        savingsDeposit.updateStatus(SavingsDepositStatus.COMPLETED);

        SavingsDeposit savedSavingsDeposit = savingsDepositRepository.save(savingsDeposit);

        return savingsDepositMapper.toDto(savedSavingsDeposit);
    }

    public void addSavingsDeposits(SavingsSubscription savingsSubscription, List<LocalDate> scheduledDates) {
        List<SavingsDeposit> depositList = new ArrayList<>();

        for (LocalDate scheduledDate : scheduledDates) {
            SavingsDeposit savingsDeposit = SavingsDeposit.builder()
                    .savingsSubscription(savingsSubscription)
                    .amount(0)
                    .scheduledDate(scheduledDate)
                    .status(SavingsDepositStatus.PENDING) // 처음에는 납입되지 않은 상태
                    .build();
            depositList.add(savingsDeposit);
        }

        savingsDepositRepository.saveAll(depositList); // 한 번에 저장
    }

    public List<SavingsDepositDTO> findFailedDeposits(Long studentId) {
        List<SavingsDeposit> failedDeposits = savingsDepositRepository.findFailedDepositsByStudent(studentId);
        return failedDeposits.stream().map(savingsDepositMapper::toDto).collect(Collectors.toList());
    }

    public int getTotalDepositAmount(List<SavingsSubscriptionDTO> savingsSubscriptionDTOList) {
        return savingsSubscriptionDTOList.stream()
                .flatMapToInt(savingsSubscriptionDTO ->
                        savingsDepositRepository.findBySavingsSubscriptionId(savingsSubscriptionDTO.getId()).stream()
                                .filter(savingsDeposit -> savingsDeposit.getStatus() == SavingsDepositStatus.COMPLETED)
                                .mapToInt(SavingsDeposit::getAmount)
                )
                .sum();
    }

    public List<SavingsDepositHistoryDTO> getSavingsDepositHistory(Long savingsSubscriptionId) {
        List<SavingsDeposit> deposits = savingsDepositRepository.findBySavingsSubscriptionId(savingsSubscriptionId);
        return deposits.stream()
                .filter(deposit -> deposit.getStatus() != SavingsDepositStatus.PENDING)
                .map(savingsDepositMapper::toHistoryDto)
                .collect(Collectors.toList());
    }

}
