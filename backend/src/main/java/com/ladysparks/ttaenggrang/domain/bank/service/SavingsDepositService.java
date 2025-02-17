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

    /**
     * 적금 납입 (자동 납입)
     * 해당 날짜에 자동으로 호출
     */
    @Transactional
    public SavingsDepositDTO updateSavingsDeposit(SavingsSubscription savingsSubscription, LocalDate depositDate, Long bankAccountId) {
        // 적금 가입 ID로 적금 가입 정보 불러오기
//        SavingsSubscription savingsSubscription = savingsSubscriptionService.findSavingsSubscriptionById(savingsSubscriptionId); // TODO: DTO로 수정
        Long savingsSubscriptionId = savingsSubscription.getId();

        // 적금 납입 내역 불러와서 depositDate에 해당하는 적금 납입 내역(List<SavingsDepositDTO>) 가져오기
        SavingsDepositDTO savingsDepositDTO = findSavingsDeposits(savingsSubscriptionId).stream()
                .filter(dto -> dto.getScheduledDate() == depositDate)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("해당 날짜에 납입해야 하는 내역이 없습니다"));

        // 그 날짜의 납입해야 하는 금액 가져옴
        int amount = savingsDepositDTO.getAmount();

        // 적금 납입 처리
        BankAccountDTO bankAccountDTO = bankAccountService.findBankAccount(bankAccountId);
        if (bankAccountDTO.getBalance() < amount) {
            throw new IllegalArgumentException("현재 은행 계좌 잔액이 부족합니다. (현재 잔액: " + bankAccountDTO.getBalance() + ")");
        }

        BankTransactionDTO bankTransactionDTO = BankTransactionDTO.builder()
                .bankAccountId(bankAccountDTO.getId())
                .type(BankTransactionType.SAVINGS_DEPOSIT)
                .amount(amount)
                .description("[적금 납입] 적금 상품: " + savingsSubscription.getSavingsProduct().getName())
                .build();
        bankTransactionService.addBankTransaction(bankTransactionDTO);

        SavingsDeposit savingsDeposit = savingsDepositMapper.toEntity(savingsDepositDTO);
        savingsDeposit.updateAmount(amount);
        savingsDeposit.updateStatus(SavingsDepositStatus.COMPLETED);

        SavingsDeposit savedSavingsDeposit = savingsDepositRepository.save(savingsDeposit);

        // TODO: SavingsSubscription의 balance 업데이트

        return savingsDepositMapper.toDto(savedSavingsDeposit);
    }

    // 적금 납입 (수동 납입)
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
                .description("[적금 납입] 적금 상품: " + savingsDeposit.getSavingsSubscription().getSavingsProduct().getName())
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
                    .status(SavingsDepositStatus.PENDING)  // 최초는 대기 상태(납입되지 않은 상태)
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

    public SavingsSubscriptionDetailDTO getSavingsDepositHistory(SavingsSubscription savingsSubscription) {
        Long savingsSubscriptionId = savingsSubscription.getId();

        List<SavingsDeposit> deposits = savingsDepositRepository.findBySavingsSubscriptionId(savingsSubscriptionId);
        List<SavingsDepositHistoryDTO> savingsDepositHistoryDTOList = deposits.stream()
                .filter(deposit -> deposit.getStatus() != SavingsDepositStatus.PENDING)
                .map(savingsDepositMapper::toHistoryDto)
                .collect(Collectors.toList());

        String savingsName = savingsSubscription.getSavingsProduct().getName();
        LocalDate startDate = savingsSubscription.getStartDate();
        LocalDate endDate = savingsSubscription.getEndDate();
        int payoutAmount =savingsSubscription.getSavingsProduct().getPayoutAmount();

        return SavingsSubscriptionDetailDTO.builder()
                .savingsName(savingsName)
                .startDate(startDate)
                .endDate(endDate)
                .depositHistory(savingsDepositHistoryDTOList)
                .payoutAmount(payoutAmount)
                .build();
    }

}
