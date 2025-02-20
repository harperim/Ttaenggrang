package com.ladysparks.ttaenggrang.domain.etf.service;

import com.ladysparks.ttaenggrang.domain.etf.dto.EtfCompositionDTO;
import com.ladysparks.ttaenggrang.domain.etf.dto.EtfDTO;
import com.ladysparks.ttaenggrang.domain.etf.entity.EtfHistory;
import com.ladysparks.ttaenggrang.domain.etf.repository.EtfHistoryRepository;
import com.ladysparks.ttaenggrang.domain.etf.repository.EtfRepository;
import com.ladysparks.ttaenggrang.domain.stock.dto.StockDTO;
import com.ladysparks.ttaenggrang.domain.stock.repository.StockHistoryRepository;
import com.ladysparks.ttaenggrang.domain.stock.service.StockService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EtfMarketStatusService {

    private final EtfRepository etfRepository;
    private final StockHistoryRepository stockHistoryRepository;
    private final EtfService etfService;
    private final EtfHistoryRepository etfHistoryRepository;
    private final StockService stockService;

    // ETF 가격 변동 계산
//
//    public void updateEtfPrices() {
//        // 모든 ETF 조회
//        List<Etf> etfs = etfRepository.findAll();
//
//        for (Etf etf : etfs) {
//            double totalChangeRate = 0;
//            for (Stock stock : etf.getStocks()) {
//                double stockChangeRate = calculateStockChangeRate(stock);
//                totalChangeRate += stockChangeRate;
//            }
//            // 구성 주식들의 단순 평균 변동률
//            double averageChangeRate = totalChangeRate / etf.getStocks().size();
//            double newPrice = etf.getPrice_per() * (1 + averageChangeRate);
//            etf.setPrice_per((int) Math.round(newPrice));
//
//            etfRepository.save(etf);
//        }
//    }
//
//    // 개별 주식의 가격 변동률 계산 (StockHistory에서 이전 가격을 가져옴)
//    private double calculateStockChangeRate(Stock stock) {
//        // 최신 StockHistory 레코드를 조회합니다.
//        StockHistory latestHistory = stockHistoryRepository.findLatestHistoryByStockId(stock.getId());
//        double previousPrice = (latestHistory != null) ? latestHistory.getPrice() : stock.getPrice_per();
//        double currentPrice = stock.getPrice_per();
//        if (previousPrice == 0) {
//            return 0; // 이전 가격이 0이면 변동률 계산 불가
//        }
//        return (currentPrice - previousPrice) / previousPrice;
//    }
    @Transactional
    public void updateEtfPrices(Long teacherId) {
        // 등록된 ETF 목록 조회
        List<EtfDTO> etfs = etfService.findEtfs(teacherId);

        for (EtfDTO etfDTO : etfs) {
            // 해당 ETF에 포함된 주식 목록 조회
            List<StockDTO> stocks = stockService.findStocksByEtfId(etfDTO.getId());

            if (stocks.isEmpty()) {
                continue; // ETF가 추종하는 주식이 없으면 건너뜀
            }

            // 개별 주식들의 변동률을 평균 내어 ETF 변동률 계산
            double totalChangeRate = 0;
            LocalDateTime latestChangeTime = null;

            for (StockDTO stock : stocks) {
                totalChangeRate += stock.getChangeRate();

                // 가장 최근의 가격 변경 시간을 추적
                if (latestChangeTime == null || stock.getPriceChangeTime().isAfter(latestChangeTime)) {
                    latestChangeTime = stock.getPriceChangeTime();
                }
            }

            double averageChangeRate = totalChangeRate / stocks.size(); // 평균 변동률

            // ETF 가격 조정 (최소 1원 유지)
            int newPrice = Math.max(1, (int) Math.round(etfDTO.getPrice_per() * (1 + averageChangeRate / 100.0)));

            // ETF DTO 업데이트
            etfDTO.setPrice_per(newPrice);
            etfDTO.setChangeRate((int) averageChangeRate);
            etfDTO.setPriceChangeTime(latestChangeTime); // 가장 최근 주식 변동 시간 반영

            // ETF 엔티티 업데이트 후 저장
            etfRepository.save(EtfDTO.toEntity(etfDTO));
        }
    }
    @Transactional
    public void recordEtfHistory(Long teacherId) {
        List<EtfDTO> etfs = etfService.findEtfs(teacherId);

        // 어제 날짜의 시작과 종료 시간 설정
        LocalDateTime startOfDay = LocalDate.now().minusDays(1).atStartOfDay();
        LocalDateTime endOfDay = LocalDateTime.of(LocalDate.now().minusDays(1), LocalTime.MAX);

        for (EtfDTO etfDTO : etfs) {
            // ETF의 조정된 가격 계산 (개별 주식 가격 및 변동률을 반영)
            int adjustedPrice = computeEtfAdjustedPrice(etfDTO, startOfDay, endOfDay);

            // 기존 ETF 가격과 비교하여 변동률 계산 (0 가격 방지)
            double priceChangeRate = etfDTO.getPrice_per() == 0 ? 0 :
                    ((double) (adjustedPrice - etfDTO.getPrice_per()) / etfDTO.getPrice_per()) * 100.0;

            // EtfHistory 저장
            EtfHistory etfHistory = EtfHistory.builder()
                    .etf(EtfDTO.toEntity(etfDTO))
                    .price(adjustedPrice)
                    // ETF의 경우 거래량 정보가 없거나 별도로 관리된다면 0 또는 해당 값을 설정
                    .buyVolume(0)
                    .sellVolume(0)
                    .priceChangeRate((int) priceChangeRate)
                    .createdAt(Timestamp.valueOf(LocalDateTime.now()))
                    .build();

            etfHistoryRepository.save(etfHistory);
        }
    }

    private int computeEtfAdjustedPrice(EtfDTO etfDTO, LocalDateTime startOfDay, LocalDateTime endOfDay) {
        double totalPrice = 0.0;
        double totalWeight = 0.0;

        for (EtfCompositionDTO composition : etfDTO.getCompositions()) {
            // 구성 주식의 현재 가격 조회
            StockDTO stockDTO = stockService.findStock(composition.getStockId())
                    .orElseThrow(() -> new IllegalArgumentException("해당 ID의 주식을 찾을 수 없습니다: " + composition.getStockId()));

            int stockPrice = stockDTO.getPricePerShare();
            double weight = composition.getWeight();

            totalPrice += stockPrice * weight;
            totalWeight += weight;
        }

        double weightedPrice = totalWeight > 0 ? totalPrice / totalWeight : 0;
        return (int) Math.round(weightedPrice);
    }


}
