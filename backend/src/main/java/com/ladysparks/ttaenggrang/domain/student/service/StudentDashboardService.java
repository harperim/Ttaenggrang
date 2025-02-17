package com.ladysparks.ttaenggrang.domain.student.service;

import com.ladysparks.ttaenggrang.domain.bank.entity.BankTransaction.BankTransactionType;
import com.ladysparks.ttaenggrang.domain.bank.service.BankAccountService;
import com.ladysparks.ttaenggrang.domain.bank.service.BankTransactionService;
import com.ladysparks.ttaenggrang.domain.bank.service.SavingsPayoutService;
import com.ladysparks.ttaenggrang.domain.bank.service.SavingsSubscriptionService;
import com.ladysparks.ttaenggrang.domain.student.dto.BankTransactionSummaryDTO;
import com.ladysparks.ttaenggrang.domain.student.dto.StudentAssetDTO;
import com.ladysparks.ttaenggrang.domain.student.dto.StudentDashboardDTO;
import com.ladysparks.ttaenggrang.domain.teacher.service.JobService;
import com.ladysparks.ttaenggrang.domain.teacher.service.NationService;
import com.ladysparks.ttaenggrang.domain.weekly_report.service.InvestmentService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentDashboardService {

    private final NationService nationService;
    private final InvestmentService investmentService;
    private final BankAccountService bankAccountService;
    private final StudentService studentService;
    private final SavingsSubscriptionService savingsSubscriptionService;
    private final JobService jobService;
    private final BankTransactionService bankTransactionService;
    private final SavingsPayoutService savingsPayoutService;

    /**
     * 학생의 대시보드 데이터 조회
     */
    public StudentDashboardDTO getStudentDashboard(Long studentId) {
        // 계좌 잔액 조회
        int bankAccountBalance = bankAccountService.findBankAccount(studentId).getBalance();

        // 적금 납입액 조회
        int savingsAmount = savingsSubscriptionService.getTotalDepositAmount(studentId);

        // 투자 평가액 조회
        int investmentAmount = investmentService.getCurrentInvestmentAmount(studentId);

        // 목표액 조회
        Long teacherId = studentService.findTeacherIdByStudentId(studentId);
        int goalAmount = nationService.findSavingsGoalAmountByTeacherId(teacherId);

        // 현재 순위 조회
        int currentRank = studentService.getSavingsAchievementRateByStudentId(studentId).getRank();

        // 총 자산 계산
        int totalAssets = savingsAmount + bankAccountBalance + investmentAmount;

        // 목표 달성률 계산
        double achievementRate = (goalAmount > 0) ? ((double) totalAssets / goalAmount) * 100 : 0.0;

        return StudentDashboardDTO.builder()
                .studentId(studentId)
                .accountBalance(bankAccountBalance)
                .currentRank(currentRank)
                .totalSavings(savingsAmount)
                .totalInvestmentAmount(investmentAmount)
                        .totalAsset(totalAssets)
                        .goalAmount(goalAmount)
                        .achievementRate(achievementRate)
                .build();
    }

    public StudentAssetDTO getStudentAsset(Long studentId) {
        // 총 급여 조회
        int totalSalary = bankTransactionService.getBankTransactionsByType(studentId, List.of(BankTransactionType.SALARY));

        // 총 저축 계산 (적금 납입 중인 금액 + 만기 지급 금액)
        int savingsDepositAmount = savingsSubscriptionService.getTotalDepositAmount(studentId);
        int savingsPayoutAmount = savingsSubscriptionService.getTotalPayoutAmount(studentId);

        int totalSavings = savingsDepositAmount + savingsPayoutAmount;

        // 투자 수익 계산 (매도 금액 + 투자 평가액)
        int totalInvestmentProfit = investmentService.getCurrentInvestmentAmount(studentId) + bankTransactionService.getBankTransactionsByType(studentId, List.of(BankTransactionType.ETF_SELL, BankTransactionType.STOCK_SELL));

        // 총 소비 조회
        int totalExpenses = bankTransactionService.getBankTransactionsByType(studentId, List.of(BankTransactionType.ITEM_BUY));

        // 인센티브 조회
        int incentive = bankTransactionService.getBankTransactionsByType(studentId, List.of(BankTransactionType.INCENTIVE));

        // 계좌 잔액 조회
        int bankAccountBalance = bankAccountService.findBankAccount(studentId).getBalance();

        // 총 자산 계산
        int totalAssets = bankAccountBalance + totalSavings + totalInvestmentProfit;

        return StudentAssetDTO.builder()
                .studentId(studentId)
                .totalAsset(totalAssets)
                .totalSalary(totalSalary)
                .totalSavings(totalSavings)
                .totalInvestmentProfit(totalInvestmentProfit)
                .totalIncentive(incentive)
                .totalExpense(totalExpenses)
                .build();
    }

    @Transactional
    public List<BankTransactionSummaryDTO> getStudentTransactionSummaryList(Long studentId) {
        return bankTransactionService.getSummaryByBankAccountId(studentId);
    }

}
