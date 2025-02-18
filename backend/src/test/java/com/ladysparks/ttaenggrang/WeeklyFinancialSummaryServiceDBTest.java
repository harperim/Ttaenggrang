package com.ladysparks.ttaenggrang;

import com.ladysparks.ttaenggrang.domain.bank.entity.BankAccount;
import com.ladysparks.ttaenggrang.domain.bank.entity.BankTransaction;
import com.ladysparks.ttaenggrang.domain.bank.entity.BankTransaction.BankTransactionType;
import com.ladysparks.ttaenggrang.domain.bank.repository.BankAccountRepository;
import com.ladysparks.ttaenggrang.domain.bank.repository.BankTransactionRepository;
import com.ladysparks.ttaenggrang.domain.student.entity.Student;
import com.ladysparks.ttaenggrang.domain.teacher.entity.Teacher;
import com.ladysparks.ttaenggrang.domain.student.repository.StudentRepository;
import com.ladysparks.ttaenggrang.domain.teacher.repository.TeacherRepository;
import com.ladysparks.ttaenggrang.domain.weekly_report.dto.WeeklyFinancialSummaryDTO;
import com.ladysparks.ttaenggrang.domain.weekly_report.entity.WeeklyFinancialSummary;
import com.ladysparks.ttaenggrang.domain.weekly_report.repository.WeeklyFinancialSummaryRepository;
import com.ladysparks.ttaenggrang.domain.weekly_report.service.WeeklyFinancialSummaryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)  // JUnit 5 스타일
@Transactional  // 테스트 후 DB 롤백 (실제 DB에 영향 X)
public class WeeklyFinancialSummaryServiceDBTest {

    @Autowired
    private WeeklyFinancialSummaryService summaryService;

    @Autowired
    private WeeklyFinancialSummaryRepository summaryRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private BankTransactionRepository bankTransactionRepository;

    @Autowired
    private BankAccountRepository bankAccountRepository;

    @Test
    public void testGenerateWeeklyReportsForAllTeachers() {
        // 1. 실제 DB에 교사 저장
        Teacher teacher = teacherRepository.save(Teacher.builder()
                .email("teacher1@email.com")
                .password("password123")
                .name("김선생")
                .school("서울초등학교")
                .createdAt(Timestamp.valueOf(LocalDateTime.now()))
                .build());

        // 2. 실제 DB에 학생 및 은행 계좌 저장
        BankAccount bankAccount = bankAccountRepository.save(BankAccount.builder()
                .accountNumber("110-12345678")
                .balance(10000)
                .createdAt(Timestamp.valueOf(LocalDateTime.now()))
                .build());

        Student student = studentRepository.save(Student.builder()
                .username("김학생")
                .password("password")
                .teacher(teacher)
                .bankAccount(bankAccount)
                .createdAt(Timestamp.valueOf(LocalDateTime.now()))
                .build());

        // 3. 실제 금융 거래 데이터 삽입 (최근 7일 거래 데이터)
        LocalDate reportDate = LocalDate.now().with(DayOfWeek.SUNDAY);
        LocalDateTime startDate = reportDate.minusDays(6).atStartOfDay();
        LocalDateTime endDate = reportDate.atTime(23, 59, 59);

        bankTransactionRepository.saveAll(List.of(
                // 총 수입 (급여 + 인센티브 + 예금이자 등)
                BankTransaction.builder().bankAccount(bankAccount).amount(3000).type(BankTransactionType.SALARY).createdAt(Timestamp.valueOf(startDate.plusDays(1))).build(),
                BankTransaction.builder().bankAccount(bankAccount).amount(1000).type(BankTransactionType.INCENTIVE).createdAt(Timestamp.valueOf(startDate.plusDays(2))).build(),
                BankTransaction.builder().bankAccount(bankAccount).amount(500).type(BankTransactionType.BANK_INTEREST).createdAt(Timestamp.valueOf(startDate.plusDays(3))).build(),

                // 저축 (적금 납입)
                BankTransaction.builder().bankAccount(bankAccount).amount(2000).type(BankTransactionType.SAVINGS_DEPOSIT).createdAt(Timestamp.valueOf(startDate.plusDays(2))).build(),

                // 투자 수익 (주식 매도)
                BankTransaction.builder().bankAccount(bankAccount).amount(1500).type(BankTransactionType.STOCK_SELL).createdAt(Timestamp.valueOf(startDate.plusDays(4))).build(),

                // 소비 지출 (아이템 구매, 세금, 벌금 등)
                BankTransaction.builder().bankAccount(bankAccount).amount(2500).type(BankTransactionType.ITEM_BUY).createdAt(Timestamp.valueOf(startDate.plusDays(5))).build(),
                BankTransaction.builder().bankAccount(bankAccount).amount(500).type(BankTransactionType.TAX).createdAt(Timestamp.valueOf(startDate.plusDays(6))).build(),
                BankTransaction.builder().bankAccount(bankAccount).amount(300).type(BankTransactionType.FINE).createdAt(Timestamp.valueOf(startDate.plusDays(7))).build()
        ));

        // 4. 주간 리포트 생성 실행
//        Map<Long, List<WeeklyFinancialSummaryDTO>> reports = summaryService.generateWeeklyReportsForAllTeachers();

        // 5. 결과 검증
//        assertNotNull(reports);
//        assertEquals(1, reports.size()); // 교사 1명
//        assertEquals(1, reports.get(teacher.getId()).size()); // 학생 1명의 리포트

        // 6. DB에서 실제 데이터 조회 후 검증
        Optional<WeeklyFinancialSummary> savedReport = summaryRepository.findByStudentIdAndReportDate(student.getId(), reportDate);
        assertTrue(savedReport.isPresent());

        WeeklyFinancialSummary report = savedReport.get();

        assertEquals(6000, report.getTotalIncome());  // 급여(3000) + 인센티브(1000) + 이자(500) + 투자 수익(1500)
        assertEquals(2500, report.getSavingsAmount()); // 적금 납입(2000) + 예금이자(500)
        assertEquals(1500, report.getInvestmentReturn()); // 주식 매도(1500)
        assertEquals(2500, report.getTotalExpenses()); // 소비(2500)
    }

}
