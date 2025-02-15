package com.ladysparks.ttaenggrang.domain.stock.service;

import com.ladysparks.ttaenggrang.domain.stock.dto.StockMarketStatusDTO;
import com.ladysparks.ttaenggrang.domain.stock.entity.StockMarketStatus;
import com.ladysparks.ttaenggrang.domain.stock.mapper.StockMarketStatusMapper;
import com.ladysparks.ttaenggrang.domain.stock.repository.StockMarketStatusRepository;
import com.ladysparks.ttaenggrang.domain.teacher.entity.Teacher;
import com.ladysparks.ttaenggrang.domain.teacher.service.TeacherService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockMarketStatusService {

    private final StockMarketStatusRepository stockMarketStatusRepository;
    private final StockMarketStatusMapper stockMarketStatusMapper;
    private final TeacherService teacherService;

    // 평일 09:00 자동 개장
    @Transactional
    public void autoMarketOpen() {
        Long teacherId = teacherService.getCurrentTeacherId();
        StockMarketStatusDTO stockMarketStatusDTO = getStockMarketStatusByTeacherId(teacherId);

        // 교사 On -> 개장
        // 교사 Off -> 폐장
        if (stockMarketStatusDTO.isTeacherOn()) {
            setStockMarketStatus(teacherId, true, true);
        } else {
            setStockMarketStatus(teacherId, false, false);
        }
    }

    // 평일 17:00 자동 폐장
    @Transactional
    public void autoMarketClose() {
        Long teacherId = teacherService.getCurrentTeacherId();
        StockMarketStatusDTO stockMarketStatusDTO = getStockMarketStatusByTeacherId(teacherId);

        // 무조건 폐장
        setStockMarketStatus(teacherId, false, stockMarketStatusDTO.isTeacherOn());
    }

    // 주식 시장 활성화/비활성화 설정 (교사 수동 설정)
    @Transactional
    public StockMarketStatusDTO setTeacherOnOff(Long teacherId, boolean open) throws BadRequestException {
        StockMarketStatusDTO stockMarketStatusDTO = getStockMarketStatusByTeacherId(teacherId);

        boolean isMarketOpen = stockMarketStatusDTO.isMarketOpen();

        LocalDate today = LocalDate.now();          // 날짜
        DayOfWeek dayOfWeek = today.getDayOfWeek(); // 요일
        LocalTime currentTime = LocalTime.now();    // 시간

        // 교사가 설정 가능한 범위(평일 9-17시)
        if (dayOfWeek != DayOfWeek.SATURDAY &&
                dayOfWeek != DayOfWeek.SUNDAY &&
                currentTime.isAfter(LocalTime.of(9, 0))
                && currentTime.isBefore(LocalTime.of(17, 0))) {
            isMarketOpen = open;
        } else {
            if (open) {
                throw new BadRequestException("개장이 불가능한 시간입니다.");
            }
        }

        return setStockMarketStatus(teacherId, isMarketOpen, open);
    }

    // 주식 시장 제어
    @Transactional
    public StockMarketStatusDTO setStockMarketStatus(Long teacherId, boolean isMarketOpen, boolean isTeacherOn) {
        Teacher teacher = teacherService.findById(teacherId)
                .orElseThrow(() -> new IllegalArgumentException("해당 교사를 찾을 수 없습니다."));

        Optional<StockMarketStatus> existingStatus = stockMarketStatusRepository.findById(teacherId);

        StockMarketStatus stockMarketStatus;

        if (existingStatus.isPresent()) {
            // 기존 엔터티 업데이트
            StockMarketStatus status = existingStatus.get();
            status.setMarketOpen(isMarketOpen);
            status.setTeacherOn(isTeacherOn);
            stockMarketStatus = stockMarketStatusRepository.save(status);
        } else {
            // 신규 엔터티 저장
            stockMarketStatus = StockMarketStatus.builder()
                    .teacher(teacher)
                    .isMarketOpen(isMarketOpen)
                    .isTeacherOn(isTeacherOn)
                    .build();
        }

        StockMarketStatus savedStockMarketStatus = stockMarketStatusRepository.save(stockMarketStatus);
        return stockMarketStatusMapper.toDto(savedStockMarketStatus);
    }

    // 현재 주식 거래 가능 여부 조회
    public StockMarketStatusDTO getStockMarketStatusByTeacherId(Long teacherId) {
        StockMarketStatus stockMarketStatus = stockMarketStatusRepository.findById(teacherId)
                .orElseGet(() -> createStockMarketStatus(teacherId)); // 없으면 생성
        return stockMarketStatusMapper.toDto(stockMarketStatus);
    }

    // 주식 시장 상태 최초 생성
    public StockMarketStatus createStockMarketStatus(Long teacherId) {
        LocalDate today = LocalDate.now();          // 날짜
        DayOfWeek dayOfWeek = today.getDayOfWeek(); // 요일
        LocalTime currentTime = LocalTime.now();    // 시간

        boolean isMarketOpen = false;
        if (dayOfWeek != DayOfWeek.SATURDAY &&
                dayOfWeek != DayOfWeek.SUNDAY &&
                currentTime.isAfter(LocalTime.of(9, 0))
                && currentTime.isBefore(LocalTime.of(17, 0))) {
            isMarketOpen = true;
        }

        Teacher teacher = teacherService.findById(teacherId)
                .orElseThrow(() -> new IllegalArgumentException("해당 교사를 찾을 수 없습니다."));

        return stockMarketStatusRepository.save(StockMarketStatus.builder()
                .teacher(teacher)
                .isMarketOpen(isMarketOpen)
                .isTeacherOn(false)
                .build());
    }

    /**
     * 시장 개장 여부
     */
    @Transactional
    public boolean isMarketOpen(Long teacherId) {
        boolean exists = stockMarketStatusRepository.existsById(teacherId);
        return exists ? stockMarketStatusRepository.findIsMarketOpenByTeacherId(teacherId) : createStockMarketStatus(teacherId).isMarketOpen();
    }

}