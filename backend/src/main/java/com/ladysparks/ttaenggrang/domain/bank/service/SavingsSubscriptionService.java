package com.ladysparks.ttaenggrang.domain.bank.service;

import com.ladysparks.ttaenggrang.domain.bank.dto.*;
import com.ladysparks.ttaenggrang.domain.bank.entity.SavingsPayout;
import com.ladysparks.ttaenggrang.domain.bank.entity.SavingsProduct;
import com.ladysparks.ttaenggrang.domain.bank.entity.SavingsSubscription;
import com.ladysparks.ttaenggrang.domain.bank.entity.SavingsSubscription.SavingsSubscriptionStatus;
import com.ladysparks.ttaenggrang.domain.bank.mapper.SavingsSubscriptionMapper;
import com.ladysparks.ttaenggrang.domain.bank.repository.SavingsSubscriptionRepository;
import com.ladysparks.ttaenggrang.domain.student.service.StudentService;
import com.ladysparks.ttaenggrang.domain.teacher.dto.StudentSavingsSubscriptionDTO;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SavingsSubscriptionService {

    private final SavingsSubscriptionRepository savingsSubscriptionRepository;
    private final SavingsSubscriptionMapper savingsSubscriptionMapper;
    private final StudentService studentService;
    private final SavingsProductService savingsProductService;
    private final SavingsDepositService savingsDepositService;
    private final SavingsPayoutService savingsPayoutService;

    // 적금 가입 [등록]
    @Transactional
    public SavingsSubscriptionDTO addSavingsSubscription(SavingsSubscriptionDTO savingsSubscriptionDTO) {

        Long studentId = studentService.getCurrentStudentId();

        // 적금 가입 중복 확인
        boolean exists = savingsSubscriptionRepository.existsByStudentIdAndSavingsProductId(
                studentId, savingsSubscriptionDTO.getSavingsProductId());
        if (exists) {
            throw new IllegalArgumentException("이미 해당 적금 상품에 가입한 학생입니다.");
        }

        // 가입할 적금 상품의 기간(주 단위) 조회
        Long durationWeeks = savingsProductService.findDurationWeeksById(
                savingsSubscriptionDTO.getSavingsProductId());

        // startDate 자동 계산 (오늘 이후 가장 가까운 `depositDayOfWeek`)
        LocalDate today = LocalDate.now();
        LocalDate startLocalDate = today.with(TemporalAdjusters.next(
                savingsSubscriptionDTO.getDepositDayOfWeek()));

        // endDate 자동 계산 (startDate + durationWeeks 주 후)
        LocalDate endLocalDate = startLocalDate.plusWeeks(durationWeeks);

        // 납입 일정 생성 (매주 지정 요일)
        List<LocalDate> depositDates = calculateDepositDates(
                startLocalDate, endLocalDate, savingsSubscriptionDTO.getDepositDayOfWeek());

        // DTO에 반영
        savingsSubscriptionDTO.setStudentId(studentId);
        savingsSubscriptionDTO.setStartDate(startLocalDate);
        savingsSubscriptionDTO.setEndDate(endLocalDate);

        // Entity 변환 및 저장
        SavingsSubscription savingsSubscription = savingsSubscriptionMapper.toEntity(savingsSubscriptionDTO);
        SavingsSubscription savedSavingsSubscription = savingsSubscriptionRepository.save(savingsSubscription);

        // DTO 반환을 위해 Deposit 일정 추가
        SavingsSubscriptionDTO savedSavingsSubscriptionDTO = savingsSubscriptionMapper.toDto(savedSavingsSubscription);
//        savedSavingsSubscriptionDTO.setDepositSchedule(depositDates);

        // depositSchedule을 기반으로 SavingsDeposit 자동 생성
        savingsDepositService.addSavingsDeposits(savedSavingsSubscription, depositDates);

        // 적금 상품 가입자 수 +1 증가
        savingsProductService.addSubscriber(savingsSubscriptionDTO.getSavingsProductId());

        // 결과 DTO 반환
        return savedSavingsSubscriptionDTO;
    }

    // startDate부터 endDate까지 매주 depositDayOfWeek에 해당하는 날짜 리스트를 반환
    private List<LocalDate> calculateDepositDates(LocalDate startDate, LocalDate endDate, DayOfWeek depositDayOfWeek) {
        List<LocalDate> depositDates = new ArrayList<>();

        // 첫 납입일 계산 (startDate 이후 가장 가까운 depositDayOfWeek)
        LocalDate firstDepositDate = startDate.with(TemporalAdjusters.nextOrSame(depositDayOfWeek));

        // 지정된 기간 동안 매주 depositDayOfWeek에 맞춰 날짜 추가
        LocalDate currentDepositDate = firstDepositDate;
        while (currentDepositDate.isBefore(endDate)) {
            depositDates.add(currentDepositDate);
            currentDepositDate = currentDepositDate.plusWeeks(1); // 다음 주 동일한 요일
        }

        return depositDates;
    }

    // 적금 가입 내역 [전체 조회]
    public List<SavingsSubscriptionDTO> findSavingsSubscriptionsByStudentId(Long studentId) {
        List<SavingsSubscription> savingsSubscriptions = savingsSubscriptionRepository.findByStudentId(studentId);

        return savingsSubscriptions.stream()
                .map(savingsSubscriptionMapper::toDto)
                .collect(Collectors.toList());
    }

    // 적금 가입 학생 내역 [전체 조회]
    public List<SavingsSubscriptionDTO> findSavingsSubscriptionsBySavingProductId(Long savingsProductId) {
        List<SavingsSubscription> savingsSubscriptions = savingsSubscriptionRepository.findBySavingsProductId(savingsProductId);

        if (savingsSubscriptions.isEmpty()) {
            throw new EntityNotFoundException("해당 적금 상품의 가입 내역이 존재하지 않습니다. ID: " + savingsProductId);
        }

        return savingsSubscriptions.stream()
                .map(savingsSubscriptionMapper::toDto)
                .collect(Collectors.toList());
    }

    public SavingsSubscriptionDetailDTO findSavingsSubscriptionById(Long savingsSubscriptionId) {
        SavingsSubscription savingsSubscription = savingsSubscriptionRepository.findById(savingsSubscriptionId)
                .orElseThrow(() -> new EntityNotFoundException("해당 적금 가입 내역이 존재하지 않습니다. ID: " + savingsSubscriptionId));

        return savingsDepositService.getSavingsDepositHistory(savingsSubscription);
    }

    public int getTotalDepositAmount(Long studentId) {
        // 1. 가입 중인 적금 상품
        List<SavingsSubscriptionDTO> savingsSubscriptionDTOList = findSavingsSubscriptionsByStudentId(studentId).stream()
                .filter(dto -> dto.getStatus() == SavingsSubscriptionStatus.ACTIVE) // 2. 상품의 가입 내역
                .toList();

        // 3. 납입 상태인 금액 합산
        return savingsDepositService.getTotalDepositAmount(savingsSubscriptionDTOList);
    }

    public int getTotalPayoutAmount(Long studentId) {
        // 1. 가입 중인 적금 상품
        List<SavingsSubscriptionDTO> savingsSubscriptionDTOList = findSavingsSubscriptionsByStudentId(studentId).stream()
                .filter(dto -> dto.getStatus() == SavingsSubscriptionStatus.MATURED || dto.getStatus() == SavingsSubscriptionStatus.WITHDRAWN) // 2. 상품의 가입 내역
                .toList();

        // 2. 만기/중도 해지로 지급된 금액 합산
        return savingsPayoutService.getTotalPayoutAmount(savingsSubscriptionDTOList);
    }

    public DepositAndSavingsCountDTO getSavingsCountByStudentId(Long studentId) {
        long depositCount = 0;
        long savingsCount = savingsSubscriptionRepository.countByStudentId(studentId);
        return DepositAndSavingsCountDTO.builder()
                .depositProductCount(depositCount)
                .savingsProductCount(savingsCount)
                .build();
    }

    public List<StudentSavingsSubscriptionDTO> findStudentSavingsSubscriptionsByStudentId(Long studentId) {
        return savingsSubscriptionRepository.findByStudentId(studentId).stream()
                .map(subscription -> {
                    SavingsProduct savingsProduct = subscription.getSavingsProduct();
                    return new StudentSavingsSubscriptionDTO(
                            subscription.getStartDate(),
                            savingsProduct.getName(),
                            savingsProduct.getAmount(),
                            savingsProduct.getInterestRate(),
                            subscription.getDepositAmount()
                    );
                })
                .collect(Collectors.toList());
    }

    /**
     * 만기 시 내부적으로 호출
     * 1. savingsSubscription의 status를 Matured로 바꿈
     * 2. savingsPayout 생성
     */
    @Transactional
    public SavingsPayoutDTO processMaturity(Long subscriptionId) {
        // 1. 적금 구독 정보 가져오기
        SavingsSubscription subscription = savingsSubscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new IllegalArgumentException("적금 가입 정보를 찾을 수 없습니다."));

        // 2. 이미 만기된 경우 예외 처리
        if (subscription.getStatus() == SavingsSubscription.SavingsSubscriptionStatus.MATURED) {
            throw new IllegalStateException("이미 만기 처리된 적금입니다.");
        }

        // 3. 적금의 상태를 MATURED로 변경
        subscription.setStatus(SavingsSubscription.SavingsSubscriptionStatus.MATURED);
        savingsSubscriptionRepository.save(subscription);

        // 4. 이자 계산 (이자율 적용)
        int principal = subscription.getDepositAmount();
        float interestRate = subscription.getSavingsProduct().getInterestRate();
        int interestAmount = Math.round(principal * interestRate);

        // 5. 지급 총액 계산
        int payoutAmount = principal + interestAmount;

        // 6. 지급 내역 생성
        SavingsPayoutDTO savingsPayout = SavingsPayoutDTO.builder()
                .savingsSubscriptionId(subscription.getId())
                .payoutAmount(payoutAmount)
                .interestAmount(interestAmount)
                .payoutDate(LocalDate.now()) // 오늘 날짜 지급
                .payoutType(SavingsPayout.SavingsPayoutType.MATURITY)
                .build();

        // 7. 지급 내역 저장
        return savingsPayoutService.createPayout(savingsPayout);
    }

    public String getSavingsProductName(Long savingsSubscriptionId) {
        return savingsSubscriptionRepository.findSavingsProductNameBySubscriptionId(savingsSubscriptionId);
    }

    public List<SavingsSubscription> findAllByMaturityDateAndStatus(LocalDate now, SavingsSubscriptionStatus savingsSubscriptionStatus) {
        return savingsSubscriptionRepository.findAllByEndDateAndStatus(now, savingsSubscriptionStatus);
    }

    public List<SavingsSubscription> findAllByStatus(SavingsSubscriptionStatus savingsSubscriptionStatus) {
        return savingsSubscriptionRepository.findAllByStatus(savingsSubscriptionStatus);
    }

}
