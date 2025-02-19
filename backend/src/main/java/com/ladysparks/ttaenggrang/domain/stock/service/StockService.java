package com.ladysparks.ttaenggrang.domain.stock.service;

import com.ladysparks.ttaenggrang.domain.stock.category.Category;
import com.ladysparks.ttaenggrang.domain.stock.category.CategoryRepository;
import com.ladysparks.ttaenggrang.domain.stock.dto.StockDTO;
import com.ladysparks.ttaenggrang.domain.stock.dto.StockSummaryDTO;
import com.ladysparks.ttaenggrang.domain.stock.entity.Stock;
import com.ladysparks.ttaenggrang.domain.stock.repository.StockRepository;
import com.ladysparks.ttaenggrang.domain.stock.repository.StockTransactionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StockService {

    private final StockRepository stockRepository;
    private final StockTransactionRepository stockTransactionRepository;
    private final CategoryRepository categoryRepository;

    @Transactional
    public StockDTO registerStock(StockDTO stockDTO) {
        // 카테고리 이름을 가져오거나 새로 생성
        if (stockDTO.getCategoryName() == null || stockDTO.getCategoryName().isEmpty()) {
            throw new IllegalArgumentException("카테고리는 필수로 선택해야 합니다.");
        }

        // 카테고리 찾기 또는 새로 생성
        Category category = categoryRepository.findByName(stockDTO.getCategoryName())
                .orElseGet(() -> {
                    Category newCategory = new Category();
                    newCategory.setName(stockDTO.getCategoryName());
                    return categoryRepository.save(newCategory);
                });

        // 주식 이름 중복 확인
        if (stockRepository.existsByName(stockDTO.getName())) {
            throw new IllegalArgumentException("이미 존재하는 주식 이름입니다.");
        }

        // DTO -> Entity 변환 후 저장
        Stock stock = Stock.builder()
                .name(stockDTO.getName())
                .price_per(stockDTO.getPricePerShare())
                .total_qty(stockDTO.getTotalQuantity())
                .remain_qty(stockDTO.getRemainQuantity())
                .description(stockDTO.getDescription())
                .created_at(Timestamp.valueOf(LocalDateTime.now()))
                .updated_at(Timestamp.valueOf(LocalDateTime.now()))

                .category(category)  // 카테고리 설정
                .build();

        // 주식 정보 저장
        Stock savedStock = stockRepository.save(stock);

        // Entity -> DTO 변환 후 반환
        return StockDTO.fromEntity(savedStock);
    }

    public List<StockDTO> findStocks(Long teacherId) {
        // 특정 교사(teacherId)와 관련된 모든 주식 데이터를 조회
        List<Stock> stocks = stockRepository.findAllByTeacher_Id(teacherId);
        // 조회된 Stock 엔티티 리스트를 StockDTO 리스트로 변환
        return stocks.stream()
                .map(StockDTO::fromEntity) // 엔티티를 DTO로 변환
                .collect(Collectors.toList()); // 변환된 DTO를 리스트로 반환
    }

    //요약 조회
    public List<StockSummaryDTO> getStockSummaryList(Long teacherId) {
        // 모든 주식 데이터 조회
        List<Stock> stocks = stockRepository.findAllByTeacher_Id(teacherId);

        // 조회된 Stock 엔티티 리스트를 StockSummaryDTO 리스트로 변환
        return stocks.stream()
                .map(stock -> {
                    // 최근 7일간 거래량 가져오기
                    int recentTransactionVolume = findTransactionVolumeForLast7Days(stock.getId());
                    // 전체 주식 평균 거래량 가져오기 (전체 평균을 계산 필요)
                    int averageTransactionVolume = findAverageTransactionVolume();
                    // 거래 활성도 계산 (예외 처리: 0으로 나누는 경우 방지)
                    int transactionFrequency = (averageTransactionVolume > 0)
                            ? (recentTransactionVolume * 100 / averageTransactionVolume)
                            : 0;

                    return StockSummaryDTO.builder()
                            .id(stock.getId())
                            .createdDate(stock.getCreated_at().toLocalDateTime().toLocalDate())
                            .name(stock.getName())
                            .type(stock.getType())
                            .category(stock.getCategory() != null ? stock.getCategory().getName() : "N/A") // null 체크 추가
                            .pricePerShare(stock.getPrice_per())
                            .priceChangeRate(stock.getChangeRate())
                            .transactionFrequency(transactionFrequency)
                            .build();
                })
                .collect(Collectors.toList());
    }

    // 최근 7일간 특정 주식의 거래량을 조회하는 메서드
    public int findTransactionVolumeForLast7Days(Long stockId) {
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
        return stockTransactionRepository.countByStockIdAndTransactionDateAfter(stockId, Timestamp.valueOf(sevenDaysAgo));
    }

    // 전체 주식의 평균 거래량을 계산하는 메서드
    public int findAverageTransactionVolume() {
        List<Integer> allTransactionVolumes = stockTransactionRepository.findAllTransactionVolumes();
        return allTransactionVolumes.isEmpty() ? 0 :
                allTransactionVolumes.stream().mapToInt(Integer::intValue).sum() / allTransactionVolumes.size();
    }

    // 특정 주식을 조회하여 StockDTO로 변환 후 반환하는 메서드
    public Optional<StockDTO> findStock(Long stockId) {
        // ID로 주식 조회 후, StockDTO로 변환하여 반환
        return stockRepository.findById(stockId)
                .map(StockDTO::fromEntity); // 엔티티를 DTO로 변환
    }

    //ETF에 포함된 주식 목록 조회
    public List<StockDTO> findStocksByEtfId(Long etfId) {
        List<Stock> stocks = stockRepository.findAllByEtf_Id(etfId);
        return stocks.stream()
                .map(StockDTO::fromEntity)
                .collect(Collectors.toList());
    }





}