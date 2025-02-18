package com.ladysparks.ttaenggrang.domain.etf.service;

import com.google.gson.Gson;
import com.ladysparks.ttaenggrang.domain.etf.dto.EtfSummaryDTO;;
import com.ladysparks.ttaenggrang.domain.etf.entity.Etf;
import com.ladysparks.ttaenggrang.domain.etf.dto.EtfDTO;
import com.ladysparks.ttaenggrang.domain.etf.repository.EtfRepository;
import com.ladysparks.ttaenggrang.domain.etf.repository.EtfTransactionRepository;
import com.ladysparks.ttaenggrang.domain.stock.category.Category;
import com.ladysparks.ttaenggrang.domain.stock.category.CategoryRepository;
import com.ladysparks.ttaenggrang.domain.stock.entity.Stock;
import com.ladysparks.ttaenggrang.domain.stock.repository.StockRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
@Service
@RequiredArgsConstructor
public class EtfService {

    private final EtfRepository etfRepository;
    private final EtfTransactionRepository etfTransactionRepository;
    private final StockRepository stockRepository;
    private final CategoryRepository categoryRepository;


    //ETF 생성
    @Transactional
    public EtfDTO createETF(String name, String category, List<Long> selectedStockIds) {
        if (selectedStockIds.size() != 3) {
            throw new IllegalArgumentException("ETF는 반드시 3개의 주식을 선택해야 합니다.");
        }

        // 선택한 주식 목록 가져오기
        List<Stock> selectedStocks = stockRepository.findAllById(selectedStockIds);

        // 카테고리 정보 가져오기
        Optional<Category> optionalCategory = categoryRepository.findByName(category); // 주어진 카테고리 이름으로 카테고리 조회

        // Optional에서 값 꺼내기
        Category etfCategory = optionalCategory.orElseThrow(() -> new IllegalArgumentException("잘못된 카테고리입니다."));

        // 선택한 주식들의 총 가격 계산
        double totalStockPrice = selectedStocks.stream()
                .mapToDouble(Stock::getPrice_per)
                .sum();

        // ETF 초기 가격 설정 (선택한 주식들의 가격 합)
        int initialPrice = (int) totalStockPrice; // 가격을 int로 변환하여 설정

        // 주식 비율 계산
        Map<Long, Double> stockRatioMap = new HashMap<>();
        for (Stock stock : selectedStocks) {
            double ratio = stock.getPrice_per() / totalStockPrice; // 비율 계산
            stockRatioMap.put(stock.getId(), ratio); // 비율 저장
        }

        // 비율을 JSON 형태로 변환
        String stockDataJson = new Gson().toJson(stockRatioMap); // Map을 JSON으로 변환

        // 각 주식의 총 수량을 평균내어 수량 계산
        int totalQuantity = selectedStocks.stream()
                .mapToInt(Stock::getTotal_qty)
                .sum(); // 선택된 주식들의 전체 수량 합

        // 평균 수량 계산
        int averageStockQuantity = totalQuantity / selectedStocks.size(); // 평균값 계산

        // 각 주식에 대해 평균 수량을 설정
        Map<Long, Integer> stockQuantityMap = new HashMap<>();
        for (Stock stock : selectedStocks) {
            stockQuantityMap.put(stock.getId(), averageStockQuantity); // 평균 수량으로 설정
        }

        // ETF 저장
        Etf etf = new Etf();
        etf.setName(name);
        etf.setPrice_per(initialPrice);  // 가격을 int로 설정
        etf.setCreated_at(Timestamp.valueOf(LocalDateTime.now()));
        etf.setStockDataJson(stockDataJson); // 비율을 JSON으로 저장
        etf.setCategory(etfCategory); // ETF에 카테고리 설정

        // ETF의 총 수량과 남은 수량을 설정
        etf.setTotal_qty(averageStockQuantity * selectedStocks.size()); // 총 수량을 평균 수량 * 선택된 주식 수로 설정
        etf.setRemain_qty(etf.getTotal_qty()); // 남은 수량 설정

        // ETF 저장
        etf = etfRepository.save(etf);
        etfRepository.flush();

        // 각 주식에 대해 수량을 설정
        for (Stock stock : selectedStocks) {
            int stockQuantity = stockQuantityMap.get(stock.getId());
            stock.setTotal_qty(stockQuantity);  // Stock 엔티티에 수량 설정
            stockRepository.save(stock);  // Stock 저장
        }

        // EtfDTO로 변환하여 반환
        return EtfDTO.fromEntity(etf);
    }



    public List<EtfDTO> findEtfs(Long teacherId) {
        //모든 주식 데이터 조회
        List<Etf> etfs = etfRepository.findAllByTeacher_Id(teacherId);
        // 조회된 ETF 엔티티 리스트를 ETFDTO 리스트로 변환
        return etfs.stream()
                .map(EtfDTO::fromEntity) // 엔티티를 DTO로 변환
                .collect(           Collectors.toList()); // 변환된 DTO를 리스트로 반환
    }

    //전체 데이터 조회
    public List<EtfSummaryDTO> getEtfSummaryList(Long teacherId) {
        // 모든 주식 데이터 조회
        List<Etf> etfs = etfRepository.findAllByTeacher_Id(teacherId);

        // 조회된 Stock 엔티티 리스트를 StockSummaryDTO 리스트로 변환
        return etfs.stream()
                .map(etf -> {
                    // 최근 7일간 거래량 가져오기
                    int recentTransactionVolume = findTransactionVolumeForLast7Days(etf.getId());
                    // 전체 주식 평균 거래량 가져오기 (전체 평균을 계산 필요)
                    int averageTransactionVolume = findAverageTransactionVolume();
                    // 거래 활성도 계산 (예외 처리: 0으로 나누는 경우 방지)
                    int transactionFrequency = (averageTransactionVolume > 0)
                            ? (recentTransactionVolume * 100 / averageTransactionVolume)
                            : 0;

                    return EtfSummaryDTO.builder()
                            .id(etf.getId())
                            .createdDate(etf.getCreated_at().toLocalDateTime().toLocalDate())
                            .name(etf.getName())
                            .type(etf.getType())
//                            .category(etf.getCategory() != null ? etf.getCategory().getName() : "N/A") // null 체크 추가
                            .price_per(etf.getPrice_per())
                            .priceChangeRate(etf.getChangeRate())
                            .transactionFrequency(transactionFrequency)
                            .build();
                })
                .collect(Collectors.toList());
    }

    // 최근 7일간 특정 주식의 거래량을 조회하는 메서드
    public int findTransactionVolumeForLast7Days(Long etfId) {
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
        return etfTransactionRepository.countByEtfIdAndTransDateAfter(etfId, Timestamp.valueOf(sevenDaysAgo));
    }

    // 전체 주식의 평균 거래량을 계산하는 메서드
    public int findAverageTransactionVolume() {
        List<Integer> allTransactionVolumes = etfTransactionRepository.findAllTransactionVolumes();
        return allTransactionVolumes.isEmpty() ? 0 :
                allTransactionVolumes.stream().mapToInt(Integer::intValue).sum() / allTransactionVolumes.size();
    }

    // 특정 주식을 조회하여 StockDTO로 변환 후 반환하는 메서드
    public Optional<EtfDTO> findEtf(Long etfId) {
        // ID로 주식 조회 후, EtfDTO 변환하여 반환
        return etfRepository.findById(etfId)
                .map(EtfDTO::fromEntity); // 엔티티를 DTO로 변환
    }





}