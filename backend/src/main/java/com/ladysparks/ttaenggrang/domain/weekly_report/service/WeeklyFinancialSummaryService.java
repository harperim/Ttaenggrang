package com.ladysparks.ttaenggrang.domain.weekly_report.service;

import com.ladysparks.ttaenggrang.domain.bank.service.BankTransactionService;
import com.ladysparks.ttaenggrang.domain.student.dto.StudentResponseDTO;
import com.ladysparks.ttaenggrang.domain.teacher.dto.TeacherResponseDTO;
import com.ladysparks.ttaenggrang.domain.student.service.StudentService;
import com.ladysparks.ttaenggrang.domain.teacher.service.TeacherService;
import com.ladysparks.ttaenggrang.domain.weekly_report.dto.FinancialGrowthDTO;
import com.ladysparks.ttaenggrang.domain.weekly_report.dto.StudentFinancialSummaryDTO;
import com.ladysparks.ttaenggrang.domain.weekly_report.dto.WeeklyFinancialSummaryDTO;
import com.ladysparks.ttaenggrang.domain.weekly_report.entity.WeeklyFinancialSummary;
import com.ladysparks.ttaenggrang.domain.weekly_report.mapper.WeeklyFinancialSummaryMapper;
import com.ladysparks.ttaenggrang.domain.weekly_report.repository.WeeklyFinancialSummaryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class WeeklyFinancialSummaryService {

    private final WeeklyFinancialSummaryRepository weeklyFinancialSummaryRepository;
    private final WeeklyFinancialSummaryMapper weeklyFinancialSummaryMapper;
    private final BankTransactionService bankTransactionService;
    private final InvestmentService investmentService;
    private final TeacherService teacherService;
    private final StudentService studentService;

    /**
     * 모든 교사의 학급 학생들의 주간 금융 리포트 생성
     */
    public Map<Long, List<WeeklyFinancialSummaryDTO>> generateWeeklyReportsForAllTeachers() {
        // 모든 교사 조회
        List<TeacherResponseDTO> teachers = teacherService.findAllTeachers();

        // 교사별 주간 금융 리포트 저장용 Map
        Map<Long, List<WeeklyFinancialSummaryDTO>> teacherReports = new HashMap<>();

        for (TeacherResponseDTO teacher : teachers) {
            List<StudentResponseDTO> students = studentService.findAllByTeacherId(teacher.getId());

            List<WeeklyFinancialSummaryDTO> studentReports = students.stream()
                    .map(student -> generateWeeklyReports(student.getId()))
                    .collect(Collectors.toList());

            teacherReports.put(teacher.getId(), studentReports);
        }

        return teacherReports;
    }

    public WeeklyFinancialSummaryDTO generateWeeklyReports(Long studentId) {
        LocalDate reportDate = LocalDate.now().with(DayOfWeek.FRIDAY); // 금요일 기준으로 변경
        LocalDateTime startDate = reportDate.minusDays(6).atStartOfDay(); // 토요일 00:00:00
        LocalDateTime endDate = reportDate.atTime(23, 59, 59); // 금요일 23:59:59

        // 각 도메인 서비스에서 데이터를 가져옴
        int totalIncome = bankTransactionService.getTotalIncome(studentId, startDate, endDate);
        int salaryAmount = bankTransactionService.getSalary(studentId, startDate, endDate);
        int savingsAmount = bankTransactionService.getSavings(studentId, startDate, endDate);
        int incentiveAmount = bankTransactionService.getIncentive(studentId, startDate, endDate);
        int totalExpenses = bankTransactionService.getTotalExpenses(studentId, startDate, endDate);
        int taxAmount = bankTransactionService.getTaxAmount(studentId, startDate, endDate);
        int fineAmount = bankTransactionService.getFineAmount(studentId, startDate, endDate);
        int totalSellAmount = bankTransactionService.getInvestmentSellAmount(studentId, startDate, endDate);

        // 투자 수익 계산 (금요일 기준)
        int currentTotalEvaluation = investmentService.getInvestmentValueByWeeksAgo(studentId, 0);
        int lastWeekTotalEvaluation = investmentService.getInvestmentValueByWeeksAgo(studentId, 1);
        int investmentReturn = totalSellAmount + currentTotalEvaluation - lastWeekTotalEvaluation;

        WeeklyFinancialSummaryDTO reportDTO = WeeklyFinancialSummaryDTO.builder()
                .studentId(studentId)
                .reportDate(reportDate)
                .totalIncome(totalIncome)
                .salaryAmount(salaryAmount)
                .savingsAmount(savingsAmount)
                .investmentReturn(investmentReturn)
                .incentiveAmount(incentiveAmount)
                .totalExpenses(totalExpenses)
                .taxAmount(taxAmount)
                .fineAmount(fineAmount)
                .build();

        // 기존 보고서 확인 (이미 존재하면 업데이트)
        Optional<WeeklyFinancialSummary> existingReport = weeklyFinancialSummaryRepository.findByStudentIdAndReportDate(studentId, reportDate);

        WeeklyFinancialSummary reportEntity;

        if (existingReport.isPresent()) {
            reportEntity = existingReport.get();
            weeklyFinancialSummaryMapper.updateFromDto(reportDTO, reportEntity);  // 업데이트 수행
        } else {
            reportEntity = weeklyFinancialSummaryMapper.toEntity(reportDTO); // 새 엔티티 생성
        }

        // 저장
        WeeklyFinancialSummary savedReport = weeklyFinancialSummaryRepository.save(reportEntity);
        return weeklyFinancialSummaryMapper.toDto(savedReport);
    }

    /**
     * 📌 특정 학생의 최근 N번째 금융 리포트 조회 (N=0이면 이번주, N=1이면 지난주, N=2이면 지지난주)
     */
    public WeeklyFinancialSummaryDTO getRecentWeeklyReport(Long studentId, int recentIndex) {
        Pageable limit = PageRequest.of(recentIndex, 1);
        List<WeeklyFinancialSummary> reports = weeklyFinancialSummaryRepository.findRecentReportsByStudentId(studentId, limit);

        if (reports.isEmpty()) {
            return WeeklyFinancialSummaryDTO.builder()
                    .savingsAmount(0)
                    .investmentReturn(0)
                    .totalExpenses(0)
                    .build();
        }

        return new WeeklyFinancialSummaryDTO(reports.get(0));
    }

    /**
     * 📌 특정 학생의 지난주 금융 성장 분석
     */
    public FinancialGrowthDTO analyzeLastWeekFinancialGrowth(Long studentId) {
        WeeklyFinancialSummaryDTO lastWeekReport = getRecentWeeklyReport(studentId, 1);
        WeeklyFinancialSummaryDTO twoWeeksAgoReport = getRecentWeeklyReport(studentId, 2);

        int lastWeekInvestmentValue = investmentService.getInvestmentValueByWeeksAgo(studentId, 1);
        int twoWeeksAgoInvestmentValue = investmentService.getInvestmentValueByWeeksAgo(studentId, 2);

        return FinancialGrowthDTO.builder()
                .savingsGrowthRate(calculateGrowthRate(twoWeeksAgoReport.getSavingsAmount(), lastWeekReport.getSavingsAmount()))
                .investmentReturnRate(calculateGrowthRate(twoWeeksAgoInvestmentValue, lastWeekInvestmentValue))
                .expenseGrowthRate(calculateGrowthRate(twoWeeksAgoReport.getTotalExpenses(), lastWeekReport.getTotalExpenses()))
                .build();
    }

    /**
     * 📌 특정 학생의 이번주 금융 성장 분석
     */
    public FinancialGrowthDTO analyzeFinancialGrowth(Long studentId) {
        WeeklyFinancialSummaryDTO thisWeekReport = getRecentWeeklyReport(studentId, 0);
        WeeklyFinancialSummaryDTO lastWeekReport = getRecentWeeklyReport(studentId, 1);

        int thisWeekInvestmentValue = investmentService.getInvestmentValueByWeeksAgo(studentId, 0);
        int lastWeekInvestmentValue = investmentService.getInvestmentValueByWeeksAgo(studentId, 1);

        return FinancialGrowthDTO.builder()
                .savingsGrowthRate(calculateGrowthRate(lastWeekReport.getSavingsAmount(), thisWeekReport.getSavingsAmount()))
                .investmentReturnRate(calculateGrowthRate(lastWeekInvestmentValue, thisWeekInvestmentValue))
                .expenseGrowthRate(calculateGrowthRate(lastWeekReport.getTotalExpenses(), thisWeekReport.getTotalExpenses()))
                .build();
    }

    /**
     * 📌 반 평균 금융 성장 분석
     */
    public FinancialGrowthDTO analyzeClassFinancialAverageGrowth(Long teacherId) {
        List<StudentResponseDTO> students = studentService.findAllByTeacherId(teacherId);

        double totalSavingsGrowth = 0;
        double totalInvestmentReturn = 0;
        double totalExpenseGrowth = 0;
        int studentCount = 0;

        for (StudentResponseDTO student : students) {
            Long studentId = student.getId();

            WeeklyFinancialSummaryDTO lastWeek = getRecentWeeklyReport(studentId, 1);
            WeeklyFinancialSummaryDTO thisWeek = getRecentWeeklyReport(studentId, 0);

            if (lastWeek == null || thisWeek == null) continue;

            totalSavingsGrowth += calculateGrowthRate(lastWeek.getSavingsAmount(), thisWeek.getSavingsAmount());
            totalInvestmentReturn += calculateGrowthRate(lastWeek.getInvestmentReturn(), thisWeek.getInvestmentReturn());
            totalExpenseGrowth += calculateGrowthRate(lastWeek.getTotalExpenses(), thisWeek.getTotalExpenses());

            studentCount++;
        }

        if (studentCount == 0) {
            return FinancialGrowthDTO.builder()
                    .savingsGrowthRate(0)
                    .investmentReturnRate(0)
                    .expenseGrowthRate(0)
                    .build();
        }

        return FinancialGrowthDTO.builder()
                .savingsGrowthRate(roundToTwoDecimalPlaces(totalSavingsGrowth / studentCount))
                .investmentReturnRate(roundToTwoDecimalPlaces(totalInvestmentReturn / studentCount))
                .expenseGrowthRate(roundToTwoDecimalPlaces(totalExpenseGrowth / studentCount))
                .build();
    }

    /**
     * ✅ 성장률 계산 메서드
     */
    private double calculateGrowthRate(int previousValue, int currentValue) {
        if (previousValue == 0) {
            return (currentValue == 0) ? 0 : 100;
        }
        return ((double) (currentValue - previousValue) / previousValue) * 100;
    }

    /**
     * ✅ 소수점 2자리까지 반올림
     */
    private double roundToTwoDecimalPlaces(double value) {
        return Math.round(value * 100) / 100.0;
    }

    /**
     * 📌 특정 학생의 지난주, 이번주, 반 평균 주간 금융 리포트 조회
     */
    public StudentFinancialSummaryDTO getStudentWeeklySummary() {
        Long studentId = studentService.getCurrentStudentId();
        Long teacherId = studentService.findTeacherIdByStudentId(studentId);

        FinancialGrowthDTO lastWeekSummary = analyzeLastWeekFinancialGrowth(studentId);
        FinancialGrowthDTO thisWeekSummary = analyzeFinancialGrowth(studentId);
        FinancialGrowthDTO classAverageSummary = analyzeClassFinancialAverageGrowth(teacherId);

        return StudentFinancialSummaryDTO.builder()
                .studentId(studentId)
                .lastWeekSummary(lastWeekSummary)
                .thisWeekSummary(thisWeekSummary)
                .classAverageSummary(classAverageSummary)
                .build();
    }

    /**
     * 📌 특정 학생의 AI 피드백 조회
     */
    public String getLatestAIFeedback(Long studentId) {
        Pageable pageable = PageRequest.of(0, 1);
        List<String> feedbackList = weeklyFinancialSummaryRepository.findLatestAIFeedbackByStudentId(studentId, pageable);
        return feedbackList.isEmpty() ? "AI 피드백이 없습니다." : feedbackList.get(0);
    }

}