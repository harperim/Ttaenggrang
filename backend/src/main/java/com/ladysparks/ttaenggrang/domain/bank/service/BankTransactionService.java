package com.ladysparks.ttaenggrang.domain.bank.service;

import com.ladysparks.ttaenggrang.domain.bank.dto.BankAccountDTO;
import com.ladysparks.ttaenggrang.domain.bank.dto.BankTransactionDTO;
import com.ladysparks.ttaenggrang.domain.bank.dto.StudentDailyAverageFinancialDTO;
import com.ladysparks.ttaenggrang.domain.bank.entity.BankAccount;
import com.ladysparks.ttaenggrang.domain.bank.entity.BankTransaction;
import com.ladysparks.ttaenggrang.domain.bank.entity.BankTransaction.BankTransactionType;
import com.ladysparks.ttaenggrang.domain.bank.mapper.BankAccountMapper;
import com.ladysparks.ttaenggrang.domain.bank.mapper.BankTransactionMapper;
import com.ladysparks.ttaenggrang.domain.bank.repository.BankTransactionRepository;
import com.ladysparks.ttaenggrang.domain.student.dto.SavingsAchievementDTO;
import com.ladysparks.ttaenggrang.domain.student.dto.StudentResponseDTO;
import com.ladysparks.ttaenggrang.domain.student.service.StudentService;
import com.ladysparks.ttaenggrang.domain.teacher.service.TeacherService;
import com.ladysparks.ttaenggrang.domain.weekly_report.dto.WeeklyReportDTO;
import com.ladysparks.ttaenggrang.global.redis.RedisGoalService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BankTransactionService {

    private final BankTransactionRepository bankTransactionRepository;
    private final BankTransactionMapper bankTransactionMapper;
    private final BankAccountMapper bankAccountMapper;
    private final TeacherService teacherService;
    private final StudentService studentService;
    private final BankAccountService bankAccountService;
    private final RedisGoalService redisGoalService;

    @Transactional
    public BankTransactionDTO addBankTransaction(BankTransactionDTO bankTransactionDTO) {
        // 기존 거래 처리
        BankTransactionDTO result = (bankTransactionDTO.getType() == BankTransactionType.TRANSFER || bankTransactionDTO.getType() == BankTransactionType.ITEM)
                ? processTransfer(bankTransactionDTO)
                : processSingleTransaction(bankTransactionDTO);

        // 목표 달성률 업데이트 & Redis 저장
//        SavingsAchievementDTO savingsAchievementDTO = studentService.calculateSavingsAchievementRate();

//        Long teacherId = studentService.findTeacherIdByStudentId(savingsAchievementDTO.getStudentId());
//        redisGoalService.saveOrUpdateGoalAchievement(teacherId, savingsAchievementDTO);

        return result;
    }

    public BankTransactionDTO processSingleTransaction(BankTransactionDTO bankTransactionDTO) {
        // 1. 거래할 계좌 조회
        BankAccountDTO bankAccountDTO = bankAccountService.findBankAccount(bankTransactionDTO.getBankAccountId());

        // 2. 거래 전 잔액 확인
        int balanceBefore = bankAccountDTO.getBalance();
        int balanceAfter = bankAccountDTO.getBalance();
        int transactionAmount = bankTransactionDTO.getAmount();

        /*
        (+)
        - 입금 → **DEPOSIT**
        - 아이템 판매 → **ITEM_SELL**
        - 주식 매도 → **STOCK_SELL**
        - ETF 매도 → **ETF_SELL**
        - 적금 이자 수령 → **SAVINGS_INTEREST**
        - 은행 계좌 이자 수령 → **BANK_INTEREST**
        - 급여 수령 → **SALARY**
        - 인센티브 수령 → **INCENTIVE**

        (-)
        - 출금 → **WITHDRAWAL**
        - 송금 -> **TRANSFER**
        - 아이템 구매 → **ITEM_BUY**
        - 주식 매수 → **STOCK_BUY**
        - ETF 매수 → **ETF_BUY**
        - 적금 납입 → **SAVINGS_DEPOSIT**
        - 세금 납부 → **TAX**
        - 벌금 납부 → **FINE**
         */

        // 3. 입금/출금 처리
        switch (bankTransactionDTO.getType()) {
            case DEPOSIT, STOCK_SELL, ETF_SELL, SAVINGS_INTEREST, BANK_INTEREST, SALARY, INCENTIVE:
                balanceAfter += transactionAmount; // 입금
                break;

            case WITHDRAW, STOCK_BUY, ETF_BUY, SAVINGS_DEPOSIT, TAX, FINE:
                if (balanceBefore < transactionAmount) {
                    throw new IllegalArgumentException("잔액이 부족합니다. 잔액: " + balanceBefore);
                }
                balanceAfter -= transactionAmount; // 출금
                break;
        }

        bankAccountDTO.setBalance(balanceAfter);
        bankTransactionDTO.setBankAccountId(bankAccountDTO.getId());
        bankTransactionDTO.setBalanceBefore(balanceBefore);
        bankTransactionDTO.setBalanceAfter(balanceAfter);
        bankTransactionDTO.setReceiverId(bankAccountDTO.getId());

        // 4. 거래 내역 저장
        BankTransaction bankTransaction = bankTransactionMapper.toEntity(bankTransactionDTO);
        bankTransactionRepository.save(bankTransaction);

        // 5. 변경된 계좌 정보 저장
        bankAccountService.updateBankAccount(bankAccountDTO);

        // 6. DTO로 반환
        return bankTransactionMapper.toDto(bankTransaction);
    }

    // 송금(Transfer) 처리 (2건의 거래 등록)
    @Transactional
    public BankTransactionDTO processTransfer(BankTransactionDTO bankTransactionDTO) {
        Long receiverId = bankTransactionDTO.getReceiverId();

        if (receiverId == null) {
            throw new IllegalArgumentException("거래 대상 학생 ID(receiverId)를 입력해주세요.");
        }

        // 1. 송신자(출금 계좌) 조회
        BankAccountDTO senderBankAccountDTO = bankAccountService.findBankAccount(bankTransactionDTO.getBankAccountId());
        Long senderBankAccountId = senderBankAccountDTO.getId();

        // 2. 수신자(입금 계좌) 조회
        Long receiverBankAccountId = studentService.findBankAccountIdById(receiverId);
        BankAccountDTO receiverBankAccountDTO = bankAccountService.findBankAccountById(receiverBankAccountId);

        if (Objects.equals(senderBankAccountId, receiverBankAccountId)) {
            throw new IllegalArgumentException("본인이 아닌 다른 학생과의 거래만 가능합니다.");
        }

        if (senderBankAccountDTO.getBalance() < bankTransactionDTO.getAmount()) {
            throw new IllegalArgumentException("잔액이 부족합니다. 현재 잔액: " + senderBankAccountDTO.getBalance());
        }

        int transferAmount = bankTransactionDTO.getAmount();
        String description = bankTransactionDTO.getDescription();

        BankTransactionType senderBankTransactionType = bankTransactionDTO.getType() == BankTransactionType.TRANSFER ? BankTransactionType.WITHDRAW : BankTransactionType.ITEM_BUY;
        BankTransactionType receiverBankTransactionType = bankTransactionDTO.getType() == BankTransactionType.TRANSFER ? BankTransactionType.DEPOSIT : BankTransactionType.ITEM_SELL;

        // 3. 출금 거래 생성
        BankTransaction senderTransaction = BankTransaction.builder()
                .bankAccount(bankAccountMapper.toUpdatedEntity(senderBankAccountDTO))
                .type(senderBankTransactionType)
                .amount(transferAmount)
                .balanceBefore(senderBankAccountDTO.getBalance())
                .balanceAfter(senderBankAccountDTO.getBalance() - transferAmount)
                .description(description)
                .build();

        // 4. 입금 거래 생성
        BankTransaction receiverTransaction = BankTransaction.builder()
                .bankAccount(bankAccountMapper.toUpdatedEntity(receiverBankAccountDTO))
                .type(receiverBankTransactionType)
                .amount(transferAmount)
                .balanceBefore(receiverBankAccountDTO.getBalance())
                .balanceAfter(receiverBankAccountDTO.getBalance() + transferAmount)
                .description(description)
                .build();

        // 5. 송금 내역 저장 (출금 & 입금 트랜잭션)
        bankTransactionRepository.save(senderTransaction);
        bankTransactionRepository.save(receiverTransaction);

        // 6. 은행 계좌 잔액 업데이트
        senderBankAccountDTO.setBalance(senderBankAccountDTO.getBalance() - transferAmount);
        receiverBankAccountDTO.setBalance(receiverBankAccountDTO.getBalance() + transferAmount);

        bankAccountService.updateBankAccount(senderBankAccountDTO);
        bankAccountService.updateBankAccount(receiverBankAccountDTO);

        return bankTransactionMapper.toDto(senderTransaction); // 출금 내역 반환
    }

    public List<BankTransactionDTO> findBankTransactions() {
        Long studentId = studentService.getCurrentStudentId();
        Long bankAccountId = studentService.findBankAccountIdById(studentId);

        return bankTransactionRepository.findByBankAccountId(bankAccountId)
                .stream()
                .map(bankTransactionMapper::toDto)
                .collect(Collectors.toList());
    }

    public WeeklyReportDTO generateWeeklyReport() {
        Long studentId = studentService.getCurrentStudentId();

        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.minusDays(7);

        // 특정 학생의 최근 7일간의 거래 내역 조회
        List<BankTransaction> transactions = bankTransactionRepository.findTransactionsByStudentIdAndDateRange(studentId, startDate, endDate);

        // 항목별 금액 계산
        int totalIncome = transactions.stream()
                .filter(t -> t.getType() == BankTransactionType.DEPOSIT
                        || t.getType() == BankTransactionType.SALARY
                        || t.getType() == BankTransactionType.ITEM_SELL
                        || t.getType() == BankTransactionType.ETF_SELL
                        || t.getType() == BankTransactionType.STOCK_SELL
                        || t.getType() == BankTransactionType.SAVINGS_INTEREST
                        || t.getType() == BankTransactionType.BANK_INTEREST
                        || t.getType() == BankTransactionType.INCENTIVE)
                .mapToInt(BankTransaction::getAmount)
                .sum();

        int salaryAmount = transactions.stream()
                .filter(t -> t.getType() == BankTransactionType.SALARY)
                .mapToInt(BankTransaction::getAmount)
                .sum();

        int savingsAmount = transactions.stream()
                .filter(t -> t.getType() == BankTransactionType.SAVINGS_DEPOSIT)
                .mapToInt(BankTransaction::getAmount)
                .sum();

        int investmentReturn = transactions.stream()
                .filter(t -> t.getType() == BankTransactionType.ETF_SELL
                        || t.getType() == BankTransactionType.STOCK_SELL)
                .mapToInt(BankTransaction::getAmount)
                .sum();

        int incentiveAmount = transactions.stream()
                .filter(t -> t.getType() == BankTransactionType.INCENTIVE)
                .mapToInt(BankTransaction::getAmount)
                .sum();

        int totalExpenses = transactions.stream()
                .filter(t -> t.getType() == BankTransactionType.WITHDRAW
                        || t.getType() == BankTransactionType.ITEM_BUY
                        || t.getType() == BankTransactionType.STOCK_BUY
                        || t.getType() == BankTransactionType.ETF_BUY)
                .mapToInt(BankTransaction::getAmount)
                .sum();

        int taxAmount = transactions.stream()
                .filter(t -> t.getType() == BankTransactionType.TAX)
                .mapToInt(BankTransaction::getAmount)
                .sum();

        int fineAmount = transactions.stream()
                .filter(t -> t.getType() == BankTransactionType.FINE)
                .mapToInt(BankTransaction::getAmount)
                .sum();

        // 한 주의 첫 거래 이전 잔액과 마지막 거래 이후 잔액을 비교(순자산 변화)
        int netBalanceChange = 0;
        if (!transactions.isEmpty()) {
            int firstBalance = transactions.get(0).getBalanceBefore();
            int lastBalance = transactions.get(transactions.size() - 1).getBalanceAfter();
            netBalanceChange = lastBalance - firstBalance;
        }

        // DTO 생성 및 반환
        return WeeklyReportDTO.builder()
                .totalIncome(totalIncome)
                .salaryAmount(salaryAmount)
                .savingsAmount(savingsAmount)
                .investmentReturn(investmentReturn)
                .incentiveAmount(incentiveAmount)
                .totalExpenses(totalExpenses)
                .taxAmount(taxAmount)
                .fineAmount(fineAmount)
                .build();
    }

    // 최근 7일 간 평균 수입, 지출
    public List<StudentDailyAverageFinancialDTO> getDailyAverageIncomeAndExpense() {
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.minusDays(7);

        // 1. 교사 ID로 해당 교사가 담당하는 학생 목록 조회
        Long teacherId = teacherService.getCurrentTeacherId();

        List<StudentResponseDTO> students = studentService.findAllByTeacherId(teacherId);

        // 2. 학생들의 bankAccountId 목록 조회
        List<Long> bankAccountIds = students.stream()
                .map(StudentResponseDTO::getBankAccount)
                .map(BankAccount::getId)
                .collect(Collectors.toList());

        if (bankAccountIds.isEmpty()) {
            throw new IllegalArgumentException("학생들의 은행 계좌 정보가 없습니다.");
        }

        // 3. 해당 학생들의 최근 7일간 거래 내역 조회
        List<BankTransaction> transactions = bankTransactionRepository.findTransactionsByBankAccountsAndDateRange(
                bankAccountIds, startDate, endDate);

        // 4. 날짜별 그룹화하여 평균 계산
        Map<LocalDate, List<BankTransaction>> transactionsByDate = transactions.stream()
                .collect(Collectors.groupingBy(t -> t.getCreatedAt().toLocalDateTime().toLocalDate()));

        List<StudentDailyAverageFinancialDTO> dailyAverages = new ArrayList<>();

        for (int i = 6; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            List<BankTransaction> dailyTransactions = transactionsByDate.getOrDefault(date, new ArrayList<>());

            double totalIncome = dailyTransactions.stream()
                    .filter(t -> t.getType() == BankTransactionType.DEPOSIT
                            || t.getType() == BankTransactionType.BANK_INTEREST
                            || t.getType() == BankTransactionType.SALARY
                            || t.getType() == BankTransactionType.ITEM_SELL
                            || t.getType() == BankTransactionType.ETF_SELL
                            || t.getType() == BankTransactionType.STOCK_SELL)
                    .mapToInt(BankTransaction::getAmount)
                    .sum();

            double totalExpense = dailyTransactions.stream()
                    .filter(t -> t.getType() == BankTransactionType.WITHDRAW
                            || t.getType() == BankTransactionType.ITEM_BUY
                            || t.getType() == BankTransactionType.ETF_BUY
                            || t.getType() == BankTransactionType.STOCK_BUY)
                    .mapToInt(BankTransaction::getAmount)
                    .sum();

            double studentCount = students.size();
            double averageIncome = studentCount > 0 ? totalIncome / studentCount : 0;
            double averageExpense = studentCount > 0 ? totalExpense / studentCount : 0;

            dailyAverages.add(new StudentDailyAverageFinancialDTO(date, averageIncome, averageExpense));
        }

        return dailyAverages;
    }

    // 주간 통계
    public int getTotalIncome(Long studentId, LocalDateTime startDate, LocalDateTime endDate) {
        return bankTransactionRepository.getTotalAmountByType(studentId, startDate, endDate,
                Arrays.asList(
                        BankTransactionType.DEPOSIT,
                        BankTransactionType.SALARY,
                        BankTransactionType.INCENTIVE,
                        BankTransactionType.ITEM_SELL,
                        BankTransactionType.ETF_SELL,
                        BankTransactionType.STOCK_SELL,
                        BankTransactionType.SAVINGS_INTEREST,
                        BankTransactionType.BANK_INTEREST
                ));
    }

    public int getSalary(Long studentId, LocalDateTime startDate, LocalDateTime endDate) {
        return bankTransactionRepository.getTotalAmountByType(studentId, startDate, endDate,
                Collections.singletonList(BankTransactionType.SALARY));
    }

    public int getSavings(Long studentId, LocalDateTime startDate, LocalDateTime endDate) {
        return bankTransactionRepository.getTotalAmountByType(studentId, startDate, endDate,
                Arrays.asList(
                        BankTransactionType.SAVINGS_DEPOSIT,
                        BankTransactionType.SAVINGS_INTEREST,
                        BankTransactionType.BANK_INTEREST
                ));
    }

    public int getIncentive(Long studentId, LocalDateTime startDate, LocalDateTime endDate) {
        return bankTransactionRepository.getTotalAmountByType(studentId, startDate, endDate,
                Collections.singletonList(BankTransactionType.INCENTIVE));
    }

    public int getTotalExpenses(Long studentId, LocalDateTime startDate, LocalDateTime endDate) {
        return bankTransactionRepository.getTotalAmountByType(studentId, startDate, endDate,
                Arrays.asList(
                        BankTransactionType.WITHDRAW,
                        BankTransactionType.ITEM_BUY,
                        BankTransactionType.STOCK_BUY,
                        BankTransactionType.ETF_BUY
                ));
    }

    public int getTaxAmount(Long studentId, LocalDateTime startDate, LocalDateTime endDate) {
        return bankTransactionRepository.getTotalAmountByType(studentId, startDate, endDate,
                Collections.singletonList(BankTransactionType.TAX));
    }

    public int getFineAmount(Long studentId, LocalDateTime startDate, LocalDateTime endDate) {
        return bankTransactionRepository.getTotalAmountByType(studentId, startDate, endDate,
                Collections.singletonList(BankTransactionType.FINE));
    }

    public int getInvestmentSellAmount(Long studentId, LocalDateTime startDate, LocalDateTime endDate) {
        return bankTransactionRepository.getTotalAmountByType(studentId, startDate, endDate,
                Arrays.asList(
                        BankTransactionType.STOCK_SELL,
                        BankTransactionType.ETF_SELL
                ));
    }

    public int getBankTransactionsByType(Long studentId, List<BankTransactionType> typeList) {
        return bankTransactionRepository.getTotalAmountByType(studentId, typeList);
    }

}
