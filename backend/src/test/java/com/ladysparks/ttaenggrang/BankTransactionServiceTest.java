package com.ladysparks.ttaenggrang;

import com.ladysparks.ttaenggrang.domain.bank.entity.BankTransaction.BankTransactionType;
import com.ladysparks.ttaenggrang.domain.bank.repository.BankTransactionRepository;
import com.ladysparks.ttaenggrang.domain.bank.service.BankTransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class BankTransactionServiceTest {

    @Autowired
    private BankTransactionService bankTransactionService;

    @MockBean // 실제 DB에 데이터를 저장하지 않고 Mock 객체를 사용해서 서비스 로직을 검증
    private BankTransactionRepository bankTransactionRepository;

    private Long studentId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    @BeforeEach
    void setup() {
        studentId = 1L;
        LocalDate reportDate = LocalDate.now().with(DayOfWeek.SUNDAY);
        startDate = reportDate.minusDays(6).atStartOfDay();
        endDate = reportDate.atTime(23, 59, 59);
    }

    @Test
    void testGetTotalIncome() {
        when(bankTransactionRepository.getTotalAmountByType(
                studentId, startDate, endDate,
                Arrays.asList(
                        BankTransactionType.DEPOSIT,
                        BankTransactionType.SALARY,
                        BankTransactionType.INCENTIVE,
                        BankTransactionType.ITEM_SELL,
                        BankTransactionType.ETF_SELL,
                        BankTransactionType.STOCK_SELL,
                        BankTransactionType.SAVINGS_INTEREST,
                        BankTransactionType.BANK_INTEREST
                ))
        ).thenReturn(4500);

        int totalIncome = bankTransactionService.getTotalIncome(studentId, startDate, endDate);
        assertEquals(4500, totalIncome);
    }

    @Test
    void testGetSalary() {
        when(bankTransactionRepository.getTotalAmountByType(
                studentId, startDate, endDate,
                Collections.singletonList(BankTransactionType.SALARY))
        ).thenReturn(3000);

        int salary = bankTransactionService.getSalary(studentId, startDate, endDate);
        assertEquals(3000, salary);
    }

    @Test
    void testGetSavingsAmount() {
        when(bankTransactionRepository.getTotalAmountByType(
                studentId, startDate, endDate,
                Arrays.asList(
                        BankTransactionType.SAVINGS_DEPOSIT,
                        BankTransactionType.SAVINGS_INTEREST,
                        BankTransactionType.BANK_INTEREST
                ))
        ).thenReturn(2000);

        int savingsAmount = bankTransactionService.getSavings(studentId, startDate, endDate);
        assertEquals(2000, savingsAmount);
    }

    @Test
    void testGetIncentive() {
        when(bankTransactionRepository.getTotalAmountByType(
                studentId, startDate, endDate,
                Collections.singletonList(BankTransactionType.INCENTIVE))
        ).thenReturn(1000);

        int incentive = bankTransactionService.getIncentive(studentId, startDate, endDate);
        assertEquals(1000, incentive);
    }

    @Test
    void testGetTotalExpenses() {
        when(bankTransactionRepository.getTotalAmountByType(
                studentId, startDate, endDate,
                Arrays.asList(
                        BankTransactionType.WITHDRAW,
                        BankTransactionType.ITEM_BUY,
                        BankTransactionType.STOCK_BUY,
                        BankTransactionType.ETF_BUY
                ))
        ).thenReturn(3300);

        int totalExpenses = bankTransactionService.getTotalExpenses(studentId, startDate, endDate);
        assertEquals(3300, totalExpenses);
    }

    @Test
    void testGetTaxAmount() {
        when(bankTransactionRepository.getTotalAmountByType(
                studentId, startDate, endDate,
                Collections.singletonList(BankTransactionType.TAX))
        ).thenReturn(500);

        int taxAmount = bankTransactionService.getTaxAmount(studentId, startDate, endDate);
        assertEquals(500, taxAmount);
    }

    @Test
    void testGetFineAmount() {
        when(bankTransactionRepository.getTotalAmountByType(
                studentId, startDate, endDate,
                Collections.singletonList(BankTransactionType.FINE))
        ).thenReturn(300);

        int fineAmount = bankTransactionService.getFineAmount(studentId, startDate, endDate);
        assertEquals(300, fineAmount);
    }

    @Test
    void testGetInvestmentSellAmount() {
        when(bankTransactionRepository.getTotalAmountByType(
                studentId, startDate, endDate,
                Arrays.asList(
                        BankTransactionType.STOCK_SELL,
                        BankTransactionType.ETF_SELL
                ))
        ).thenReturn(1500);

        int investmentSellAmount = bankTransactionService.getInvestmentSellAmount(studentId, startDate, endDate);
        assertEquals(1500, investmentSellAmount);
    }

}
