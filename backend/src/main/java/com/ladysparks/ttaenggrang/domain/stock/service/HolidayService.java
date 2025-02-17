package com.ladysparks.ttaenggrang.domain.stock.service;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Service
public class HolidayService {
    // 하드코딩된 한국의 공휴일 목록
    public boolean isHoliday(LocalDate date) {
        List<LocalDate> holidays = Arrays.asList(
                LocalDate.of(date.getYear(), 1, 1),   // 새해 첫날 (1월 1일)
                LocalDate.of(date.getYear(), 3, 1),   // 삼일절 (3월 1일)
                LocalDate.of(date.getYear(), 5, 5),   // 어린이날 (5월 5일)
                LocalDate.of(date.getYear(), 6, 6),   // 현충일 (6월 6일)
                LocalDate.of(date.getYear(), 8, 15),  // 광복절 (8월 15일)
                LocalDate.of(date.getYear(), 10, 3),  // 개천절 (10월 3일)
                LocalDate.of(date.getYear(), 10, 9),  // 한글날 (10월 9일)
                LocalDate.of(date.getYear(), 12, 25), // 크리스마스 (12월 25일)
                // 추가적인 공휴일
                LocalDate.of(date.getYear(), 1, 24),  // 설날 (음력 1월 1일, 예시로 1월 24일 하드코딩)
                LocalDate.of(date.getYear(), 9, 30)   // 추석 (음력 8월 15일, 예시로 9월 30일 하드코딩)
        );

        return holidays.contains(date);
    }
}
