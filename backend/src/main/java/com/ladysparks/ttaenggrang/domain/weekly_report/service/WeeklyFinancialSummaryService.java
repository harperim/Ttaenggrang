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
     * 🕑 매주 금요일 오전 2시에 자동 실행
     */
    @Scheduled(cron = "0 0 2 ? * FRI", zone = "Asia/Seoul")
    public void scheduleWeeklyReportGeneration() {
        log.info("📢 [주간 금융 리포트 생성 시작] - 실행 시간: {}", LocalDateTime.now());

        Map<Long, List<WeeklyFinancialSummaryDTO>> reports = generateWeeklyReportsForAllTeachers();

        log.info("✅ [주간 금융 리포트 생성 완료] - 총 {}명의 교사 데이터 생성됨", reports.size());
    }

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
     * 특정 학생의 최근 N번째 금융 리포트 조회 (N=0이면 이번주, N=1이면 지난주, N=2이면 지지난주)
     */
    public WeeklyFinancialSummaryDTO getRecentWeeklyReport(Long studentId, int recentIndex) {
        Pageable limit = PageRequest.of(recentIndex, 1); // 한 번에 한 개만 가져오기
        List<WeeklyFinancialSummary> reports = weeklyFinancialSummaryRepository.findRecentReportsByStudentId(studentId, limit);

        if (reports.isEmpty()) {
            return null;
        }

        return new WeeklyFinancialSummaryDTO(reports.get(0)); // 최근 N 번째 데이터 반환
    }

    /**
     * 특정 날짜의 주간 금융 리포트 조회
     */
    public WeeklyFinancialSummaryDTO getWeeklyReport(Long studentId, int weeksAgo) {
        // 기준이 되는 금요일 날짜 계산
        LocalDate reportDate = LocalDate.now().with(DayOfWeek.FRIDAY).minusWeeks(weeksAgo);

        // 특정 날짜의 주간 금융 리포트 조회
        return weeklyFinancialSummaryRepository.findByStudentIdAndReportDate(studentId, reportDate)
                .map(WeeklyFinancialSummaryDTO::new)
                .orElse(null);
    }

    public WeeklyFinancialSummaryDTO getThisWeekReport() {
        Long studentId = studentService.getCurrentStudentId();
        return getRecentWeeklyReport(studentId, 0);
    }

    /**
     * 특정 학생의 지난주 금융 성장 분석
     */
    public FinancialGrowthDTO analyzeLastWeekFinancialGrowth(Long studentId) {
        LocalDate lastWeekReportDate = LocalDate.now().with(DayOfWeek.FRIDAY).minusWeeks(1);
        LocalDate twoWeeksAgoReportDate = lastWeekReportDate.minusWeeks(1);

        // 지난주 & 2주 전 금융 리포트 조회
        WeeklyFinancialSummaryDTO lastWeekReport = getRecentWeeklyReport(studentId, 1);
        WeeklyFinancialSummaryDTO twoWeeksAgoReport = getRecentWeeklyReport(studentId, 2);

        // 지난주 & 2주 전 투자 평가액 조회
        int lastWeekInvestmentValue = investmentService.getInvestmentValueByWeeksAgo(studentId, 1);
        int twoWeeksAgoInvestmentValue = investmentService.getInvestmentValueByWeeksAgo(studentId, 2);

        // 증가율 계산
        double savingsGrowthRate = calculateGrowthRate(lastWeekReport.getSavingsAmount(), twoWeeksAgoReport.getSavingsAmount());
        double investmentReturnRate = calculateGrowthRate(lastWeekInvestmentValue, twoWeeksAgoInvestmentValue);
        double expenseGrowthRate = calculateGrowthRate(lastWeekReport.getTotalExpenses(), twoWeeksAgoReport.getTotalExpenses());

        return FinancialGrowthDTO.builder()
                .savingsGrowthRate(savingsGrowthRate)
                .investmentReturnRate(investmentReturnRate)
                .expenseGrowthRate(expenseGrowthRate)
                .build();
    }

    /**
     * 특정 학생의 이번주 저축 증가율, 투자 수익률, 지출 증가율 계산
     */
    public FinancialGrowthDTO analyzeFinancialGrowth(Long studentId) {
        LocalDate thisWeekReportDate = LocalDate.now().with(DayOfWeek.FRIDAY);
        LocalDate lastWeekReportDate = thisWeekReportDate.minusWeeks(1);

        // 이번 주와 지난 주 금융 리포트 조회
        WeeklyFinancialSummaryDTO thisWeekReport = getRecentWeeklyReport(studentId, 0);
        WeeklyFinancialSummaryDTO lastWeekReport = getRecentWeeklyReport(studentId, 1);

        // 이번 주 & 지난 주 투자 평가액 조회
        int thisWeekInvestmentValue = investmentService.getInvestmentValueByWeeksAgo(studentId, 0);
        int lastWeekInvestmentValue = investmentService.getInvestmentValueByWeeksAgo(studentId, 1);

        // 증가율 계산 (지난 주 값이 0이면 증가율 0으로 처리)
        double savingsGrowthRate = calculateGrowthRate(thisWeekReport.getSavingsAmount(), lastWeekReport.getSavingsAmount());
        double investmentReturnRate = calculateGrowthRate(thisWeekInvestmentValue, lastWeekInvestmentValue);
        double expenseGrowthRate = calculateGrowthRate(thisWeekReport.getTotalExpenses(), lastWeekReport.getTotalExpenses());

        return FinancialGrowthDTO.builder()
                .savingsGrowthRate(savingsGrowthRate)
                .investmentReturnRate(investmentReturnRate)
                .expenseGrowthRate(expenseGrowthRate)
                .build();
    }

    /**
     * 특정 반(teacherId)의 학생들의 평균 저축 증가율, 투자 수익률, 지출 증가율 계산
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

            if (lastWeek != null && thisWeek != null) {
                totalSavingsGrowth += calculateGrowthRate(lastWeek.getSavingsAmount(), thisWeek.getSavingsAmount());
                totalInvestmentReturn += calculateGrowthRate(lastWeek.getInvestmentReturn(), thisWeek.getInvestmentReturn());
                totalExpenseGrowth += calculateGrowthRate(lastWeek.getTotalExpenses(), thisWeek.getTotalExpenses());
                studentCount++;
            }
        }

        // 증가율 계산
        double savingsGrowthRate = totalSavingsGrowth / studentCount;
        double investmentReturnRate = totalInvestmentReturn / studentCount;
        double expenseGrowthRate = totalExpenseGrowth / studentCount;

        if (studentCount == 0) return new FinancialGrowthDTO(); // 학생이 없는 경우 0 반환

        return FinancialGrowthDTO.builder()
                .savingsGrowthRate(savingsGrowthRate)
                .investmentReturnRate(investmentReturnRate)
                .expenseGrowthRate(expenseGrowthRate)
                .build();
    }

    /**
     * 증가율 계산 함수
     */
    private double calculateGrowthRate(int current, int previous) {
        if (previous == 0) {
            return 0.0; // 이전 값이 0이면 증가율을 0으로 설정 (분모가 0인 경우 방지)
        }
        return ((double) (current - previous) / previous) * 100;
    }

    /**
     * 특정 학생의 지난주, 이번주, 반 평균 주간 금융 리포트 조회
     */
    public StudentFinancialSummaryDTO getStudentWeeklySummary() {
        // 학생 정보 조회
        Long studentId = studentService.getCurrentStudentId();
        Long teacherId = studentService.findTeacherIdByStudentId(studentId);

        // 주간 금융 리포트 조회
        FinancialGrowthDTO lastWeekSummary = analyzeLastWeekFinancialGrowth(studentId);
        FinancialGrowthDTO thisWeekSummary = analyzeFinancialGrowth(studentId);

        // 반 평균 주간 금융 리포트 조회
        FinancialGrowthDTO classAverageSummary = analyzeClassFinancialAverageGrowth(teacherId);

        return StudentFinancialSummaryDTO.builder()
                .studentId(studentId)
                .lastWeekSummary(lastWeekSummary)
                .thisWeekSummary(thisWeekSummary)
                .classAverageSummary(classAverageSummary)
                .build();
    }

    /**
     * 📌 특정 학생의 최신 AI 피드백 조회
     */
    public String getLatestAIFeedback(Long studentId) {
        Pageable pageable = PageRequest.of(0, 1);
        List<String> feedbackList = weeklyFinancialSummaryRepository.findLatestAIFeedbackByStudentId(studentId, pageable);

        return feedbackList.isEmpty() ? "AI 피드백이 없습니다." : feedbackList.get(0);
    }

}
