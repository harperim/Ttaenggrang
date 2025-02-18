package com.ladysparks.ttaenggrang.domain.etf.service;

import com.ladysparks.ttaenggrang.domain.bank.dto.BankTransactionDTO;
import com.ladysparks.ttaenggrang.domain.bank.entity.BankTransaction.BankTransactionType;
import com.ladysparks.ttaenggrang.domain.bank.service.BankTransactionService;
import com.ladysparks.ttaenggrang.domain.etf.dto.EtfSummaryDTO;
import com.ladysparks.ttaenggrang.domain.etf.dto.EtfTransactionDTO;
import com.ladysparks.ttaenggrang.domain.etf.entity.Etf;
import com.ladysparks.ttaenggrang.domain.etf.dto.EtfDTO;
import com.ladysparks.ttaenggrang.domain.etf.entity.EtfTransaction;
import com.ladysparks.ttaenggrang.domain.etf.entity.TransType;
import com.ladysparks.ttaenggrang.domain.etf.repository.EtfRepository;
import com.ladysparks.ttaenggrang.domain.etf.repository.EtfTransactionRepository;
import com.ladysparks.ttaenggrang.domain.stock.dto.StockDTO;
import com.ladysparks.ttaenggrang.domain.stock.dto.StockSummaryDTO;
import com.ladysparks.ttaenggrang.domain.stock.entity.Stock;
import com.ladysparks.ttaenggrang.domain.stock.entity.TransactionType;
import com.ladysparks.ttaenggrang.domain.stock.repository.StockHistoryRepository;
import com.ladysparks.ttaenggrang.domain.stock.repository.StockRepository;
import com.ladysparks.ttaenggrang.domain.stock.repository.StockTransactionRepository;
import com.ladysparks.ttaenggrang.domain.student.entity.Student;
import com.ladysparks.ttaenggrang.domain.student.repository.StudentRepository;
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
public class EtfService {

    private final EtfRepository etfRepository;
    private final EtfTransactionRepository etfTransactionRepository;
    private final StudentRepository studentRepository;
    private final StockHistoryRepository stockHistoryRepository;
    private final BankTransactionService bankTransactionService;
    private final StockRepository stockRepository;
    private final StockTransactionRepository stockTransactionRepository;

//    // ETF 생성
//// 사용자 보유 주식 수량 계산
//    public EtfDTO createETF(EtfDTO etfDTO) {
//        List<Stock> selectedStocks = stockRepository.findAllById(etfDTO.getStockIds());
//
//        if (selectedStocks.size() < 3) {
//            throw new IllegalArgumentException("ETF를 만들기 위해서는 최소 3개의 주식을 선택해야 합니다.");
//        }
//
//        // 주식들의 총 가치를 계산하여 ETF의 가격을 설정
//        double totalValue = 0;
//        for (Stock stock : selectedStocks) {
//            totalValue += stock.getPrice_per();  // 주식 가격을 반영하여 총 가치를 계산
//        }
//
//        // ETF 생성
//        Etf etf = new Etf();
//        etf.setName(etfDTO.getName());  // ETF 이름
//        etf.setDescription(etfDTO.getDescription());  // ETF 설명
//        etf.setPrice_per((int) totalValue);  // ETF 가격
////        etf.setTotal_qty(etfDTO.getQ());  // 선생님이 설정한 수량
////        etf.setRemain_qty(etfDTO.getQuantity());  // 초기 남은 수량 (선생님 설정한 값)
//        etf.setCreated_at(Timestamp.valueOf(LocalDateTime.now()));  // 생성일
//        etf.setUpdated_at(Timestamp.valueOf(LocalDateTime.now()));  // 수정일
//
//        // ETF 저장
//        Etf savedEtf = etfRepository.save(etf);
//
//        // 각 주식에 대한 비율(weight) 설정
//        for (Stock stock : selectedStocks) {
//            BigDecimal weight = new BigDecimal(stock.getPrice_per())
//                    .divide(new BigDecimal(totalValue), 4, RoundingMode.HALF_UP);  // 비율 계산
//            stock.setWeight(weight);  // 주식의 비율 설정
//            stockRepository.save(stock);  // 주식 정보 저장
//        }
//
//        // 생성된 ETF DTO 반환
//        return EtfDTO.fromEntity(savedEtf);
//    }
//}
//
//    // 사용자 보유 주식 수량 계산
//    public int getUserOwnedStockQty(Long studentId, Long stockId) {
//        List<StockTransaction> transactions = stockTransactionRepository.findByStudentIdAndStockId(studentId, stockId);
//        int ownedQty = 0;
//
//        // 거래내역을 순회하면서 구매한 수량만 더합니다
//        for (StockTransaction transaction : transactions) {
//            if (transaction.getTransType() == TransType.BUY) {
//                ownedQty += transaction.getOwned_qty();
//            }
//        }
//
//        return ownedQty;  // 최종 보유 수량 리턴
//    }
    //목록 조회
//
//    public  Long saveEtf(EtfDTO etfDto) {
//        // EtfDTO를 Etf 엔티티로 변환
//        Etf etf = EtfDTO.toEntity(etfDto);
//        // 변환된 엔티티를 DB에 저장
//        etfRepository.save(etf);
//        // 저장된 엔티티의 ID 반환
//        return etf.getId();
//    }

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


    //가격 변동
//    @Transactional
//    public List<ChangeResponseDTO> updateEtfPricesForMarketOpening() {
//        List<Stock> stocks = stockRepository.findAll();
//        List<ChangeResponseDTO> stockDTOList = new ArrayList<>();
//
//        for (Stock stock : stocks) {
//            double oldPrice = stock.getPrice_per();
//            try {
//                // ETF 가격 변동 처리
//                updateEtfPrice(etf); // ETF 가격 업데이트
//
//                double newPrice = stock.getPrice_per(); // 업데이트된 가격
//                double changeRate = calculateChangeRate(oldPrice, newPrice); // 변동률 계산
//
//                stockDTOList.add(ChangeResponseDTO.builder()
//                        .id(stock.getId())
//                        .name(stock.getName())
//                        .price_per((int) newPrice)
//                        .changeRate((int) changeRate)
//                        .total_qty(stock.getTotal_qty())
//                        .remain_qty(stock.getRemain_qty())
//                        .description(stock.getDescription())
//                        .build());
//            } catch (Exception e) {
//                System.err.println("ETF 가격 업데이트 중 오류 발생: " + e.getMessage());
//                throw new IllegalArgumentException("ETF 가격 업데이트 중 오류 발생: " + e.getMessage(), e);
//            }
//        }
//
//        return stockDTOList;
//    }
//
//    // ETF 가격 업데이트 로직
//    private EtfDTO updateEtfPrice(Etf etf) {
//        LocalDate yesterday = LocalDate.now().minusDays(1);
//        int dailyBuyVolume = stockTransactionRepository.getBuyVolumeForStockYesterday(etf.getId(), TransType.BUY, yesterday);
//        int dailySellVolume = stockTransactionRepository.getSellVolumeForStockYesterday(etf.getId(), TransType.SELL, yesterday);
//
//        double oldPrice = etf.getPrice_per();
//
//        if (dailyBuyVolume == 0 && dailySellVolume == 0) {
//            System.out.println("거래량 없음, 가격 유지: " + etf.getName());
//            etf.setChangeRate(0);
//        } else {
//            // 매수량, 매도량에 따른 새로운 가격 계산 (ETF 비중 고려)
//            double newPrice = calculateEtfPriceChange(etf, dailyBuyVolume, dailySellVolume);
//            etf.setPrice_per((int) Math.round(newPrice));
//            etf.setPriceChangeTime(LocalDateTime.now());
//            System.out.println("ETF 가격 변동 적용: " + etf.getName());
//
//            // 변동률 계산 및 적용
//            double changeRate = calculateChangeRate(oldPrice, newPrice);
//            etf.setChangeRate((int) (Math.round(changeRate * 100) / 100.0));
//        }
//
//        StockHistory history = new StockHistory();
//        history.setStock(stock);
//        history.setPrice(stock.getPrice_per());
//        history.setBuyVolume(dailyBuyVolume);
//        history.setSellVolume(dailySellVolume);
//        history.setDate(Timestamp.valueOf(LocalDateTime.now().minusDays(1)));
//        stockHistoryRepository.save(history);
//
//        etfRepository.save(etf);
//        System.out.println("ETF 가격 업데이트 완료: " + etf.getName());
//
//        return EtfDTO.fromEntity(etf);
//    }
//
//
//    // ETF 가격 변동 계산: 주식의 가격 변동과 ETF의 비중을 반영하여 새로운 ETF 가격 계산
//    public double calculateEtfPriceChange(Stock stock, int dailyBuyVolume, int dailySellVolume) {
//        double currentPrice = stock.getPrice_per();
//        double weight = stock.getWeight().doubleValue();
//
//        if (dailyBuyVolume == 0 && dailySellVolume == 0) {
//            return currentPrice; // 거래량이 없으면 가격 유지
//        }
//
//        try {
//            // 매수량, 매도량에 따른 가격 변동률 계산
//            double changeRate = (double) (dailyBuyVolume - dailySellVolume) / (dailyBuyVolume + dailySellVolume);
//            changeRate = Math.max(-0.10, Math.min(0.10, changeRate)); // ±10%로 제한
//            double newPrice = currentPrice * (1 + changeRate);
//
//            // ETF의 주식 가격 변동을 반영하여 ETF 가격 계산
//            double weightedPriceChange = newPrice * weight;
//            return weightedPriceChange;
//        } catch (ArithmeticException e) {
//            throw new IllegalArgumentException("매수량과 매도량의 합이 0입니다. 가격 계산이 불가능합니다.");
//        }
//    }




}