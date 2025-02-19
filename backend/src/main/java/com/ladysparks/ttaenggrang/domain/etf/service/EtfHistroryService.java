package com.ladysparks.ttaenggrang.domain.etf.service;


import com.ladysparks.ttaenggrang.domain.etf.dto.EtfHistoryDTO;
import com.ladysparks.ttaenggrang.domain.etf.entity.Etf;
import com.ladysparks.ttaenggrang.domain.etf.entity.EtfHistory;
import com.ladysparks.ttaenggrang.domain.etf.repository.EtfHistoryRepository;
import com.ladysparks.ttaenggrang.domain.etf.repository.EtfRepository;
import com.ladysparks.ttaenggrang.domain.stock.dto.ChangeResponseDTO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;


import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class EtfHistroryService {
    private final EtfRepository etfRepository;
    private final EtfHistoryRepository etfHistoryRepository;

    // ETF 가격 변동 처리 (폐장 시 적용)
    @Transactional
    public List<ChangeResponseDTO> updateETFPricesForMarketOpening() {
        List<Etf> etfs = etfRepository.findAll();
        List<ChangeResponseDTO> etfDTOList = new ArrayList<>();

        for (Etf etf : etfs) {
            etfRepository.save(etf);

            double oldPrice = etf.getPrice_per();
            try {
                // 17시에 가격 변동 계산
                if (isMarketClosing()) {
                    updateETFPriceForNextDay(etf); // 9시에 적용될 가격 계산
                }

                double newPrice = etf.getPrice_per(); // 9시에 적용된 가격
                double changeRate = calculateChangeRate(oldPrice, newPrice); // 변동률 계산

                etfDTOList.add(ChangeResponseDTO.builder()
                        .id(etf.getId())
                        .name(etf.getName())
                        .pricePerShare((int) newPrice)
                        .changeRate((int) changeRate)
                        .totalQuantity(etf.getTotal_qty())
                        .remainQuantity(etf.getRemain_qty())
                        .description(etf.getDescription())
                        .build());
            } catch (Exception e) {
                System.err.println("ETF 가격 업데이트 중 오류 발생: " + e.getMessage());
                throw new IllegalArgumentException("ETF 가격 업데이트 중 오류 발생: " + e.getMessage(), e);
            }
        }

        return etfDTOList;
    }

    // 17시 이후에 내일 9시에 적용될 변동된 가격 계산
    private void updateETFPriceForNextDay(Etf etf) {
        LocalDateTime endTime = LocalDateTime.of(LocalDate.now(), LocalTime.of(17, 0));
        LocalDateTime nextMorning = LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.of(9, 0));

        // ETF가 추적하는 지수나 자산군의 가격 변동을 반영하여 새로운 가격 계산
        double[] priceAndChangeRate = calculatePriceChangeBasedOnIndex(etf.getTrackedIndex(), etf.getPrice_per());
        double newPrice = priceAndChangeRate[0];
        double changeRate = priceAndChangeRate[1];

        // 변동된 가격과 변동률을 저장
        etf.setPrice_per((int) Math.round(newPrice));
        etf.setChangeRate((int) Math.round(changeRate));
        etf.setPriceChangeTime(nextMorning); // 9시에 적용될 시간

        // 가격 변동 이력 저장
        EtfHistory history = new EtfHistory();
        history.setEtf(etf);
        history.setPrice(etf.getPrice_per());
        history.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
        etfHistoryRepository.save(history);

        // ETF 정보 저장
        etfRepository.save(etf);
    }

    // ETF가 추적하는 지수나 자산군의 가격 변동에 따른 가격 변동 계산
    public double[] calculatePriceChangeBasedOnIndex(String trackedIndex, double currentPrice) {
        // trackedIndex에 해당하는 지수나 자산군의 가격 변동률을 가져와서 계산
        double changeRate = getIndexChangeRate(trackedIndex);
        double newPrice = currentPrice * (1 + changeRate);

        return new double[]{newPrice, changeRate * 100}; // 변동률을 100배 해서 퍼센트로 반환
    }

    // 지수나 자산군의 가격 변동률 가져오기 (예시)
    private double getIndexChangeRate(String trackedIndex) {
        // 실제로는 외부 API나 데이터베이스에서 지수의 가격 변동률을 가져오는 로직이 필요
        // 여기서는 예시로 0.5% 상승했다고 가정
        return 0.005;
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

    // 특정 ETF의 가격 변동 이력 조회
    public List<EtfHistoryDTO> getEtfHistoryByEtfId(Long etfId) {
        if (etfId == null || etfId <= 0) {
            throw new IllegalArgumentException("잘못된 ETF ID입니다: " + etfId);
        }

        List<EtfHistory> historyList = etfHistoryRepository.findByEtfId(etfId);
        if (historyList.isEmpty()) {
            throw new IllegalArgumentException("해당 ETF ID에 대한 가격 변동 이력이 없습니다: " + etfId);
        }

        // EtfHistory 엔티티를 EtfHistoryDTO로 변환하여 반환
        return historyList.stream()
                .map(EtfHistoryDTO::fromEntity)
                .collect(Collectors.toList());
    }

// 모든 ETF 가격 변동 이력 조회
    /**
     * 📌 특정 교사가 관리하는 ETF의 최근 5개 평일 변동 이력 조회 (오늘 포함, 주말 제외, 오래된 순서)
     */
    public Map<Long, List<EtfHistoryDTO>> getLast5WeekdaysEtfHistory(Long teacherId) {
        List<Etf> etfs = etfRepository.findByTeacherId(teacherId); // 교사가 관리하는 모든 ETF 조회
        Map<Long, List<EtfHistoryDTO>> historyMap = new HashMap<>();

        Pageable pageable = (Pageable) PageRequest.of(0, 5); // 최신 5개만 조회

        for (Etf etf : etfs) {
            List<EtfHistory> histories = etfHistoryRepository.findLast5WeekdaysIncludingToday(etf.getId(), (org.springframework.data.domain.Pageable) pageable);
            List<EtfHistoryDTO> historyDTOs = histories.stream()
                    .map(EtfHistoryDTO::fromEntity)
                    .sorted(Comparator.comparing(EtfHistoryDTO::getDate)) // 오래된 순서로 정렬
                    .collect(Collectors.toList());
            historyMap.put(etf.getId(), historyDTOs);
        }
        return historyMap;
    }

}
