package com.ladysparks.ttaenggrang.domain.weekly_report.service;

import com.ladysparks.ttaenggrang.domain.etf.repository.EtfRepository;
import com.ladysparks.ttaenggrang.domain.stock.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class InvestmentService {

    private final EtfRepository etfRepository;
    private final StockRepository stockRepository;

//    public int getCurrentInvestmentValue(Long studentId) {
//        int stockValue = stockRepository.getCurrentStockEvaluation(studentId);
//        int etfValue = etfRepository.getCurrentEtfEvaluation(studentId);
//        return stockValue + etfValue;
//    }

//    public int getLastWeekInvestmentValue(Long studentId) {
//        LocalDateTime endDate = LocalDateTime.now().minusDays(7);
//        LocalDateTime startDate = endDate.minusDays(7);
//
//        int lastWeekStockValue = stockRepository.getLastWeekStockEvaluation(studentId, startDate, endDate);
//        int lastWeekEtfValue = etfRepository.getLastWeekEtfEvaluation(studentId, startDate, endDate);
//
//        return lastWeekStockValue + lastWeekEtfValue;
//    }

    /**
     * 특정 주차 기준으로 투자 평가액 조회 (금요일 기준)
     * @param studentId  학생 ID
     * @param weeksAgo   몇 주 전 데이터를 조회할지 (0 = 이번 주, 1 = 지난주, 2 = 지지난주)
     * @return 해당 주차의 총 투자 평가액
     */
    public int getInvestmentValueByWeeksAgo(Long studentId, int weeksAgo) {
        // 기준 주차의 마지막 금요일을 찾기
        LocalDate targetFriday = LocalDate.now().minusWeeks(weeksAgo).with(DayOfWeek.FRIDAY);
        LocalDateTime startDate = targetFriday.minusDays(6).atStartOfDay();  // 해당 주의 토요일 00:00:00
        LocalDateTime endDate = targetFriday.atTime(23, 59, 59);  // 해당 주의 금요일 23:59:59

        int stockValue = stockRepository.getStockEvaluationByDate(studentId, startDate, endDate)
                .orElse(0);
        int etfValue = etfRepository.getEtfEvaluationByDate(studentId, startDate, endDate)
                .orElse(0);

        return stockValue + etfValue;
    }

    public int getInvestmentValue(Long studentId, LocalDate targetDate) {
        // 해당 날짜가 속한 주의 금요일 찾기
        LocalDate targetFriday = targetDate.with(DayOfWeek.FRIDAY);
        LocalDateTime startDate = targetFriday.minusDays(6).atStartOfDay();  // 해당 주의 토요일 00:00:00
        LocalDateTime endDate = targetFriday.atTime(23, 59, 59);  // 해당 주의 금요일 23:59:59

        int stockValue = stockRepository.getStockEvaluationByDate(studentId, startDate, endDate).orElse(0);
        int etfValue = etfRepository.getEtfEvaluationByDate(studentId, startDate, endDate).orElse(0);

        return stockValue + etfValue;
    }

    /**
     * 특정 학생의 현재 투자 평가액 조회 (현재 날짜 기준)
     */
    public int getCurrentInvestmentAmount(Long studentId) {
        LocalDateTime now = LocalDateTime.now();  // 현재 시간

        int stockValue = stockRepository.getStockEvaluationByDate(studentId, now, now).orElse(0);
        int etfValue = etfRepository.getEtfEvaluationByDate(studentId, now, now).orElse(0);

        return stockValue + etfValue;
    }

}
