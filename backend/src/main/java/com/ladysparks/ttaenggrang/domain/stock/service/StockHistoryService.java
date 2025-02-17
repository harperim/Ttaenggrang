package com.ladysparks.ttaenggrang.domain.stock.service;

import com.ladysparks.ttaenggrang.domain.stock.dto.ChangeResponseDTO;
import com.ladysparks.ttaenggrang.domain.stock.dto.StockHistoryDTO;
import com.ladysparks.ttaenggrang.domain.stock.entity.Stock;
import com.ladysparks.ttaenggrang.domain.stock.entity.StockHistory;
import com.ladysparks.ttaenggrang.domain.stock.entity.TransactionType;
import com.ladysparks.ttaenggrang.domain.stock.repository.StockHistoryRepository;
import com.ladysparks.ttaenggrang.domain.stock.repository.StockRepository;
import com.ladysparks.ttaenggrang.domain.stock.repository.StockTransactionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StockHistoryService {

    private final StockHistoryRepository stockHistoryRepository;
    private final StockRepository stockRepository;
    private final StockTransactionRepository stockTransactionRepository;

    // -----------------------------------------------------------------------------------------------------------------

    // 가격 변동 처리 (폐장 시 적용)
    @Transactional
    public List<ChangeResponseDTO> updateStockPricesForMarketOpening() {
        List<Stock> stocks = stockRepository.findAll();
        List<ChangeResponseDTO> stockDTOList = new ArrayList<>();

        for (Stock stock : stocks) {
            stockRepository.save(stock);

            double oldPrice = stock.getPrice_per();
            try {
                // 17시에 가격 변동 계산
                if (isMarketClosing()) {
                    updateStockPriceForNextDay(stock); // 9시에 적용될 가격 계산
                }

                double newPrice = stock.getPrice_per(); // 9시에 적용된 가격
                double changeRate = calculateChangeRate(oldPrice, newPrice); // 변동률 계산

                stockDTOList.add(ChangeResponseDTO.builder()
                        .id(stock.getId())
                        .name(stock.getName())
                        .pricePerShare((int) newPrice)
                        .changeRate((int) changeRate)
                        .totalQuantity(stock.getTotal_qty())
                        .remainQuantity(stock.getRemain_qty())
                        .description(stock.getDescription())
                        .build());
            } catch (Exception e) {
                System.err.println("주식 가격 업데이트 중 오류 발생: " + e.getMessage());
                throw new IllegalArgumentException("주식 가격 업데이트 중 오류 발생: " + e.getMessage(), e);
            }
        }

        return stockDTOList;
    }

    // 17시 이후에 내일 9시에 적용될 변동된 가격 계산
    private void updateStockPriceForNextDay(Stock stock) {
        LocalDateTime endTime = LocalDateTime.of(LocalDate.now(), LocalTime.of(17, 0));
        LocalDateTime nextMorning = LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.of(9, 0));

        // 9시부터 17시까지의 거래량 조회
        int dailyBuyVolume = stockTransactionRepository.getBuyVolumeForStockInTimeRange(stock.getId(), TransactionType.BUY, endTime, nextMorning);
        int dailySellVolume = stockTransactionRepository.getSellVolumeForStockInTimeRange(stock.getId(), TransactionType.SELL, endTime, nextMorning);

        if (dailyBuyVolume == 0 && dailySellVolume == 0) {
            stock.setChangeRate(0);  // 변동률 0%
        } else {
            double[] priceAndChangeRate = calculatePriceChangeBasedOnTransaction(dailyBuyVolume, dailySellVolume, stock.getPrice_per());
            double newPrice = priceAndChangeRate[0];
            double changeRate = priceAndChangeRate[1];

            // 변동된 가격과 변동률을 저장
            stock.setPrice_per((int) Math.round(newPrice));
            stock.setChangeRate((int) Math.round(changeRate));
            stock.setPriceChangeTime(nextMorning); // 9시에 적용될 시간
        }

        // 가격 변동 이력 저장
        StockHistory history = new StockHistory();
        history.setStock(stock);
        history.setPrice(stock.getPrice_per());
        history.setBuyVolume(dailyBuyVolume);
        history.setSellVolume(dailySellVolume);
        history.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
        stockHistoryRepository.save(history);

        // 주식 정보 저장
        stockRepository.save(stock);
    }

    // 매수량, 매도량에 따른 가격 변동 계산
    public double[] calculatePriceChangeBasedOnTransaction(int buyVolume, int sellVolume, double currentPrice) {
        if (buyVolume == 0 && sellVolume == 0) {
            return new double[]{currentPrice, 0}; // 가격 유지, 변동률 0%
        }

        double changeRate = (double) (buyVolume - sellVolume) / (buyVolume + sellVolume);
        double newPrice = currentPrice * (1 + changeRate);

        return new double[]{newPrice, changeRate * 100}; // 변동률을 100배 해서 퍼센트로 반환
    }

    // 가격 변동률 계산
    private double calculateChangeRate(double oldPrice, double newPrice) {
        if (oldPrice == 0) {
            return 0; // 기존 가격이 0이면 변동률을 계산할 수 없음
        }
        return ((newPrice - oldPrice) / oldPrice) * 100;
    }

    // 시장 종료 시점 확인 (17시 이후)
    private boolean isMarketClosing() {
        LocalTime currentTime = LocalTime.now();
        return currentTime.isAfter(LocalTime.of(17, 0)); // 17시 이후
    }

    // 특정 주식의 가격 변동 이력 조회
    public List<StockHistoryDTO> getStockHistoryByStockId(Long stockId) {
        if (stockId == null || stockId <= 0) {
            throw new IllegalArgumentException("잘못된 주식 ID입니다: " + stockId);
        }

        List<StockHistory> historyList = stockHistoryRepository.findByStockId(stockId);
        if (historyList.isEmpty()) {
            throw new IllegalArgumentException("해당 주식 ID에 대한 가격 변동 이력이 없습니다: " + stockId);
        }

        // StockHistory 엔티티를 StockHistoryDTO로 변환하여 반환
        return historyList.stream()
                .map(StockHistoryDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // 모든 주식 가격 변동 이력 조회
    public List<StockHistoryDTO> getAllStockHistory() {
        List<StockHistory> historyList = stockHistoryRepository.findAll();
        if (historyList.isEmpty()) {
            throw new IllegalArgumentException("등록된 주식 가격 변동 이력이 없습니다.");
        }

        // StockHistory 엔티티를 StockHistoryDTO로 변환하여 반환
        return historyList.stream()
                .map(StockHistoryDTO::fromEntity)
                .collect(Collectors.toList());
    }

}
