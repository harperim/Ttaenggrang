package com.ladysparks.ttaenggrang.domain.stock.service;

import com.ladysparks.ttaenggrang.domain.stock.dto.StockDTO;
import com.ladysparks.ttaenggrang.domain.stock.dto.StockMarketStatusDTO;
import com.ladysparks.ttaenggrang.domain.stock.entity.Stock;
import com.ladysparks.ttaenggrang.domain.stock.entity.StockHistory;
import com.ladysparks.ttaenggrang.domain.stock.entity.StockMarketStatus;
import com.ladysparks.ttaenggrang.domain.stock.mapper.StockMarketStatusMapper;
import com.ladysparks.ttaenggrang.domain.stock.repository.StockHistoryRepository;
import com.ladysparks.ttaenggrang.domain.stock.repository.StockMarketStatusRepository;
import com.ladysparks.ttaenggrang.domain.stock.repository.StockRepository;
import com.ladysparks.ttaenggrang.domain.teacher.dto.TeacherResponseDTO;
import com.ladysparks.ttaenggrang.domain.teacher.entity.Teacher;
import com.ladysparks.ttaenggrang.domain.teacher.service.TeacherService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockMarketStatusService {

    private final StockMarketStatusRepository stockMarketStatusRepository;
    private final StockMarketStatusMapper stockMarketStatusMapper;
    private final TeacherService teacherService;
    private final StockService stockService;
    private final StockTransactionService stockTransactionService;
    private final StockHistoryRepository stockHistoryRepository;
    private final StockRepository stockRepository;

    // 🕔 평일 09:00 자동 개장
    @Transactional
    @Scheduled(cron = "${scheduling.stock-market.open}")
//    @Scheduled(cron = "0 0 9 * * ?")
    public void autoMarketOpen() {
        for (TeacherResponseDTO teacher : teacherService.findAllTeachers()) {
            Long teacherId = teacher.getId();

            // 주가 변동 반영
            updateStockPrices(teacherId);

            // STOCK_HISTORY 기반으로 현재 주가 갱신
            StockMarketStatusDTO stockMarketStatusDTO = getStockMarketStatusByTeacherId(teacherId);

            // 교사 On -> 개장
            // 교사 Off -> 폐장
            if (stockMarketStatusDTO.isTeacherOn()) {
                setStockMarketStatus(teacherId, true, true);
            } else {
                setStockMarketStatus(teacherId, false, false);
            }
        }
    }

    // 🕔 평일 17:00 자동 폐장
    @Scheduled(cron = "${scheduling.stock-market.close}")
//    @Scheduled(cron = "0 0 17 * * ?")
    @Transactional
    public void autoMarketClose() {
        for (TeacherResponseDTO teacher : teacherService.findAllTeachers()) {
            Long teacherId = teacher.getId();

            StockMarketStatusDTO stockMarketStatusDTO = getStockMarketStatusByTeacherId(teacherId);

            // 무조건 폐장
            setStockMarketStatus(teacherId, false, stockMarketStatusDTO.isTeacherOn());

            // 오늘의 주식 거래량 조회 & 가격 변동량 계산 -> STOCK_HISTORY에 저장
            recordStockHistory(teacherId);
        }
    }

    /**
     * 매일 17시 (주식 시장 폐장)
     * - 해당 날짜의 주식 거래량 조회
     * - 가격 변동량 계산
     * - STOCK_HISTORY 테이블에 저장
     */
    @Transactional
    public void recordStockHistory(Long teacherId) {
        List<StockDTO> stocks = stockService.findStocks(teacherId);
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);

        for (StockDTO stockDTO : stocks) {
            int buyVolume = stockTransactionService.getTotalBuyVolume(stockDTO.getId(), startOfDay, endOfDay);
            int sellVolume = stockTransactionService.getTotalSellVolume(stockDTO.getId(), startOfDay, endOfDay);
            int totalVolume = buyVolume + sellVolume;

            // 최대 ±10% 변동
            double maxChangeRate = 10.0;
            double priceChangeRate = (totalVolume == 0) ? 0 : ((double) (buyVolume - sellVolume) / totalVolume) * maxChangeRate;

            // 변동된 가격 반영 (새 가격 = 현재 가격 * (1 + 변동률 %))
            int adjustedPrice = (int) (stockDTO.getPricePerShare() * (1 + priceChangeRate / 100.0));

            // StockHistory 저장
            StockHistory stockHistory = StockHistory.builder()
                    .stock(StockDTO.toEntity(stockDTO))
                    .price(adjustedPrice)
                    .buyVolume(buyVolume)
                    .sellVolume(sellVolume)
                    .priceChangeRate((int) priceChangeRate)
                    .createdAt(Timestamp.valueOf(LocalDateTime.now()))
                    .build();

            stockHistoryRepository.save(stockHistory);
        }
    }

    /**
     * 매일 9시 (주식 시장 개장)
     * - 어제 저장된 STOCK_HISTORY 기반으로 현재 가격과 변동률 업데이트
     */
    @Transactional
    public void updateStockPrices(Long teacherId) {
        List<StockDTO> stocks = stockService.findStocks(teacherId); // 모든 주식 조회

        for (StockDTO stockDTO : stocks) {
            // 해당 주식의 가장 최근 거래 내역 조회 (어제 기록된 데이터)
            StockHistory latestHistory = stockHistoryRepository.findLatestHistoryByStockId(stockDTO.getId());

            if (latestHistory != null) {
                // 변동률을 반영한 새로운 주가 계산
                int newPrice = stockDTO.getPricePerShare() + (int) (stockDTO.getPricePerShare() * (latestHistory.getPriceChangeRate() / 100.0));

                // stock 엔티티 업데이트
                stockDTO.setPricePerShare(newPrice);
                stockDTO.setChangeRate(latestHistory.getPriceChangeRate());
                stockDTO.setPriceChangeTime(LocalDateTime.now());

                stockRepository.save(StockDTO.toEntity(stockDTO)); // 업데이트된 주식 정보 저장
            }
        }
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