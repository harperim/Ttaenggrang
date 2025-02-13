package com.ladysparks.ttaenggrang.domain.stock.service;

import com.ladysparks.ttaenggrang.domain.stock.category.Category;
import com.ladysparks.ttaenggrang.domain.stock.category.CategoryRepository;
import com.ladysparks.ttaenggrang.domain.bank.dto.BankTransactionDTO;
import com.ladysparks.ttaenggrang.domain.bank.entity.BankTransaction.BankTransactionType;
import com.ladysparks.ttaenggrang.domain.bank.service.BankTransactionService;
import com.ladysparks.ttaenggrang.domain.stock.dto.ChangeResponseDTO;
import com.ladysparks.ttaenggrang.domain.stock.dto.StockTransactionDTO;
import com.ladysparks.ttaenggrang.domain.stock.dto.StudentStockDTO;
import com.ladysparks.ttaenggrang.domain.stock.entity.*;
import com.ladysparks.ttaenggrang.domain.stock.dto.StockDTO;
import com.ladysparks.ttaenggrang.domain.stock.repository.StockHistoryRepository;
import com.ladysparks.ttaenggrang.domain.stock.repository.StockRepository;
import com.ladysparks.ttaenggrang.domain.stock.repository.StockTransactionRepository;
import com.ladysparks.ttaenggrang.domain.student.entity.Student;
import com.ladysparks.ttaenggrang.domain.student.repository.StudentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StockService {

    private final StockRepository stockRepository; //의존성 주입

    private final StockHistoryRepository stockHistoryRepository;

    private final StockTransactionRepository stockTransactionRepository;
    //학생
    private final StudentRepository studentRepository;

    private final BankTransactionService bankTransactionService;

    private final CategoryRepository categoryRepository;
    private final HolidayService holidayService;


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
                .price_per(stockDTO.getPrice_per())
                .total_qty(stockDTO.getTotal_qty())
                .remain_qty(stockDTO.getRemain_qty())
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


    //목록 조회
    public Long saveStock(StockDTO stockDto) {
        // StockDTO를 Stock 엔티티로 변환
        Stock stock = StockDTO.toEntity(stockDto);
        // 변환된 엔티티를 DB에 저장
        stockRepository.save(stock);
        // 저장된 엔티티의 ID 반환
        return stock.getId();
    }

    public List<StockDTO> findStocks() {
        //모든 주식 데이터 조회
        List<Stock> stocks = stockRepository.findAll();
        // 조회된 Stock 엔티티 리스트를 StockDTO 리스트로 변환
        return stocks.stream()
                .map(StockDTO::fromEntity) // 엔티티를 DTO로 변환
                .collect(Collectors.toList()); // 변환된 DTO를 리스트로 반환
    }

    public Optional<StockDTO> findStock(Long stockId) {
        // ID로 주식 조회 후, StockDTO로 변환하여 반환
        return stockRepository.findById(stockId)
                .map(StockDTO::fromEntity); // 엔티티를 DTO로 변환
    }

    public List<StockDTO> getFilteredStocks(List<Long> stockId) {
        // 주식 ID 리스트를 사용하여 해당 주식들을 조회
        List<Stock> stocks = stockRepository.findAllById(stockId);

        // 조회된 주식들을 StockDTO로 변환하여 반환
        return stocks.stream()
                .map(StockDTO::fromEntity) // Stock 엔티티를 StockDTO로 변환
                .collect(Collectors.toList()); // DTO 리스트 반환


    }


    // 주식 매수 로직
    @Transactional
    public StockTransactionDTO buyStock(Long stockId, int shareCount, Long studentId) {
        // 주식 정보 가져오기
        Optional<Stock> stockOptional = stockRepository.findById(stockId);
        if (stockOptional.isEmpty()) {
            throw new IllegalArgumentException("주식이 존재하지 않습니다.");
        }
        Stock stock = stockOptional.get();


        // 학생 정보 가져오기
        Optional<Student> studentOptional = studentRepository.findById(studentId);
        if (studentOptional.isEmpty()) {
            throw new IllegalArgumentException("학생이 존재하지 않습니다.");
        }
        Student student = studentOptional.get();


        // 구매 가능한 수량 확인
        if (shareCount <= 0) {
            throw new IllegalArgumentException("0 이하 수량은 매수할 수 없습니다.");
        }

        // 남은 수량 확인
        if (stock.getRemain_qty() < shareCount) {
            throw new IllegalArgumentException("남은 수량이 부족합니다.");
        }


        // 주식 현재 가격 가져오기
        int price_per = stock.getPrice_per();
        if (price_per <= 0) {
            throw new IllegalStateException("주식 가격이 설정되지 않았습니다.");
        }

        // 총 금액 계산
        int totalAmount = price_per * shareCount;

        // 로그로 값 확인
        System.out.println("현재 주식 가격: " + price_per);
        System.out.println("총 구매 금액: " + totalAmount);

        //은행 계좌에서 금액 차감 (API 호출)
        Long bankAccountId = student.getBankAccount().getId();
        BankTransactionDTO transactionRequest = new BankTransactionDTO();
        transactionRequest.setBankAccountId(bankAccountId);
        transactionRequest.setType(BankTransactionType.STOCK_BUY);
        transactionRequest.setAmount(totalAmount);
        transactionRequest.setDescription("주식 매수: " + stock.getName());

        BankTransactionDTO bankTransactionDTO = bankTransactionService.addBankTransaction(transactionRequest);

        // 은행 서비스에서 받은 최종 잔액 확인
        int balanceAfter = bankTransactionDTO.getBalanceAfter();
        System.out.println("주식 매수 완료, 남은 잔액: " + balanceAfter);


        // 주식의 재고 수량 차감
        stock.setRemain_qty(stock.getRemain_qty() - shareCount);
        stockRepository.save(stock);


        // 학생이 현재 보유한 해당 주식 수량 조회
        Integer owned_qty = stockTransactionRepository.findTotalSharesByStudentAndStock(studentId, stockId.intValue(), TransType.BUY);
        if (owned_qty == null) {
            owned_qty = 0; // 처음 구매라면 0으로 설정
        }

        // 기존 보유량 + 새로 매수한 수량
        int updatedOwnedQty = owned_qty + shareCount;


        // 새로운 매수 거래 생성
        StockTransaction transaction = new StockTransaction();
        transaction.setStock(stock);
        transaction.setStudent(student);
        transaction.setShare_count(shareCount);
        transaction.setTransType(TransType.BUY);
        transaction.setTrans_date(new Timestamp(System.currentTimeMillis()));  //날짜
        transaction.setOwned_qty(updatedOwnedQty); // 기존 보유량 + 새로 매수한 수량
        transaction.setTotal_amt(totalAmount);
        transaction.setPurchase_prc(price_per); // 현재 가격을 그대로 저장

        stockTransactionRepository.save(transaction);

        // 주식의 현재 가격을 업데이트
        stock.setPrice_per(price_per);
        stockRepository.save(stock);

        return StockTransactionDTO.fromEntity(transaction, updatedOwnedQty);
    }

    // 주식 매도 로직
    @Transactional
    public StockTransactionDTO sellStock(Long stockId, int shareCount, Long studentId) {
        // 주식 정보 가져오기
        Stock stock = stockRepository.findById(stockId)
                .orElseThrow(() -> new IllegalArgumentException("주식이 존재하지 않습니다."));

        // 학생 정보 가져오기
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("학생이 존재하지 않습니다."));


        // 매도 가능한 수량 확인
        if (shareCount <= 0) {
            throw new IllegalArgumentException("0 이하 수량은 매도할 수 없습니다.");
        }

        //학생의 총 매수량(BUY)과 총 매도량(SELL) 조회
        Integer totalBought = stockTransactionRepository.findTotalSharesByStudentAndStock(studentId, stockId.intValue(), TransType.BUY);
        Integer totalSold = stockTransactionRepository.findTotalSharesByStudentAndStock(studentId, stockId.intValue(), TransType.SELL);

        // NULL 방지 처리
        totalBought = (totalBought == null) ? 0 : totalBought;
        totalSold = (totalSold == null) ? 0 : totalSold;

        // 현재 보유량 = 총 매수량 - 총 매도량
        int owned_qty = totalBought - totalSold;

        // 보유량보다 더 많이 매도하려는 경우 예외 발생
        if (owned_qty < shareCount) {
            throw new IllegalArgumentException("보유한 주식 수량이 부족합니다.");
        }

        // 주식 현재 가격 가져오기
        int price_per = stock.getPrice_per();
        if (price_per <= 0) {
            throw new IllegalStateException("주식 가격이 설정되지 않았습니다.");
        }

        // 총 매도 금액 계산
        int totalAmount = price_per * shareCount;

        // 로그 확인
        System.out.println("현재 주식 가격: " + price_per);
        System.out.println("총 매도 금액: " + totalAmount);

        //은행 계좌에서 금액 차감 (API 호출)
        Long bankAccountId = student.getBankAccount().getId();
        BankTransactionDTO transactionRequest = new BankTransactionDTO();
        transactionRequest.setBankAccountId(bankAccountId);
        transactionRequest.setType(BankTransactionType.STOCK_SELL);
        transactionRequest.setAmount(totalAmount);
        transactionRequest.setDescription("주식 매도: " + stock.getName());

        BankTransactionDTO bankTransactionDTO = bankTransactionService.addBankTransaction(transactionRequest);

        // 은행 서비스에서 받은 최종 잔액 확인
        int balanceAfter = bankTransactionDTO.getBalanceAfter();
        System.out.println("주식 매도 완료, 남은 잔액: " + balanceAfter);


        stock.setRemain_qty(stock.getRemain_qty() + shareCount);
        stockRepository.save(stock);


        int updatedOwnedQty = owned_qty - shareCount;


        StockTransaction transaction = new StockTransaction();
        transaction.setStock(stock);
        transaction.setStudent(student);
        transaction.setShare_count(shareCount);
        transaction.setTransType(TransType.SELL); // 매도 타입
        transaction.setTrans_date(new Timestamp(System.currentTimeMillis())); // 거래 날짜
        transaction.setOwned_qty(updatedOwnedQty); // 매도 후 남은 보유량 저장
        transaction.setTotal_amt(totalAmount);
        transaction.setPurchase_prc(price_per); // 현재 가격 저장

        // 매도 거래 저장
        stockTransactionRepository.save(transaction);

        // DTO 변환 후 반환
        return StockTransactionDTO.fromEntity(transaction, updatedOwnedQty);
    }

    //학생이 보유 하고 있는 주식 조회
    public List<StudentStockDTO> getStudentStocks(Long studentId) {
        List<StockTransaction> transactions = stockTransactionRepository.findByStudentId(Math.toIntExact(studentId));

        Map<Stock, Integer> stockHoldings = new HashMap<>();
        Map<Stock, Integer> stockPurchasePrice = new HashMap<>();
        Map<Stock, LocalDateTime> stockPurchaseDate = new HashMap<>();

        for (StockTransaction tx : transactions) {
            Stock stock = tx.getStock();
            int qty = tx.getShare_count();

            if (tx.getTransType() == TransType.BUY) {
                stockHoldings.put(stock, stockHoldings.getOrDefault(stock, 0) + qty);

                // 최초 구매 가격과 날짜 저장
                if (!stockPurchasePrice.containsKey(stock)) {
                    stockPurchasePrice.put(stock, tx.getPurchase_prc());
                    stockPurchaseDate.put(stock, tx.getTrans_date().toLocalDateTime());
                }
            } else if (tx.getTransType() == TransType.SELL) {
                stockHoldings.put(stock, stockHoldings.getOrDefault(stock, 0) - qty);
            }
        }

        List<StudentStockDTO> studentStocks = new ArrayList<>();
        for (Map.Entry<Stock, Integer> entry : stockHoldings.entrySet()) {
            Stock stock = entry.getKey();
            int holdingQty = entry.getValue();

            if (holdingQty > 0) { // 보유 수량이 있는 경우만 추가
                studentStocks.add(new StudentStockDTO(
                        stock.getId(),
                        stock.getName(),
                        holdingQty,
                        stockPurchasePrice.get(stock), // 최초 구매 가격
                        stockPurchaseDate.get(stock), // 최초 구매 날짜
                        stock.getPrice_per() // 현재 주가
                ));
            }
        }
        return studentStocks;
    }

//
//
//
//    // 주식 시장 관리 (개장 or 폐장) 및 가격 변동 처리
//    public boolean manageMarket(boolean openMarket) {
//        LocalDateTime today = LocalDateTime.now();
//        LocalTime currentTime = today.toLocalTime();
//
//        // 주말 및 공휴일 확인
//        if (today.getDayOfWeek().getValue() >= 6) { // 토요일 또는 일요일
//            throw new IllegalArgumentException("주말에는 주식시장이 열리지 않습니다.");
//        }
//        if (holidayService.isHoliday(today.toLocalDate())) {
//            throw new IllegalArgumentException("오늘은 공휴일 또는 예약된 휴장일입니다.");
//        }
//
//        List<Stock> stocks = stockRepository.findAll();  // Stock 객체들을 DB에서 조회
//        if (stocks.isEmpty()) {
//            throw new IllegalArgumentException("주식 목록이 비어 있습니다.");
//        }
//
//        // 주식 시장 열렸다고 출력
//        if (openMarket) {
//            System.out.println("주식 시장이 열렸습니다.");
//            for (Stock stock : stocks) {
//                stock.setIsMarketActive(true);  // 시장 개장
//            }
//        } else {
//            // 주식 시장 비활성화 처리
//            System.out.println("주식 시장이 비활성화되었습니다.");
//            for (Stock stock : stocks) {
//                stock.setIsMarketActive(false); // 시장 비활성화
//            }
//        }
//
//        // 거래량 계산 및 가격 변동 처리
//        for (Stock stock : stocks) {
//            if (stock.getIsMarketActive()) { // 시장이 활성화 상태일 때
//                // 개장 시간과 폐장 시간 확인 후 거래량 계산
//                if (currentTime.isAfter(stock.getOpenTime()) && currentTime.isBefore(stock.getCloseTime())) {
//                    // 거래량 계산 (매수량 및 매도량)
//                    int totalBuyVolumeInRange = stockHistoryRepository.getTotalBuyVolumeInRange(stock.getId(), stock.getOpenTime(), stock.getCloseTime());
//                    int totalSellVolumeInRange = stockHistoryRepository.getTotalSellVolumeInRange(stock.getId(), stock.getOpenTime(), stock.getCloseTime());
//
//                    // 가격 변동 및 거래 기록 저장
//                    Stock updatedStock = updateStockPrice(stock, totalBuyVolumeInRange, totalSellVolumeInRange);  // 가격 변동
//                    saveStockHistory(stock, totalBuyVolumeInRange, totalSellVolumeInRange);  // 거래 기록 저장
//
//                    System.out.println(stock.getName() + " 주식 가격이 변동되었습니다.");
//                    System.out.println("새 가격: " + updatedStock.getPrice_per() + "원");
//                }
//            }
//        }
//
//        stockRepository.saveAll(stocks);  // DB에 업데이트된 주식 정보 저장
//        return openMarket;
//    }


    // 가격 변동 처리 (폐장 시 적용)
    @Transactional
    public List<ChangeResponseDTO> updateStockPricesForMarketOpening() {
        // 모든 주식 정보를 불러오고, 시장 활성 상태를 고정 (9시~17시에는 true)
        List<Stock> stocks = stockRepository.findAll();
        List<ChangeResponseDTO> stockDTOList = new ArrayList<>();

        for (Stock stock : stocks) {
            // 시장은 고정적으로 활성(true)로 설정 (개장 시간, 폐장 시간 변경 불가)
            stock.setIsMarketActive(true);
            stockRepository.save(stock);

            double oldPrice = stock.getPrice_per();
            try {
                updateStockPrice(stock); // 개별 주식 가격 업데이트

                double newPrice = stock.getPrice_per(); // 업데이트된 가격
                double changeRate = calculateChangeRate(oldPrice, newPrice); // 변동률 계산

                // ETF 또는 일반 주식 정보에 필요한 필드들을 DTO로 설정
                stockDTOList.add(ChangeResponseDTO.builder()
                        .id(stock.getId())
                        .name(stock.getName())
                        .price_per((int) newPrice)
                        .changeRate((int) changeRate)
                        .isMarketActive(stock.getIsMarketActive())
                        .total_qty(stock.getTotal_qty())
                        .remain_qty(stock.getRemain_qty())
                        .description(stock.getDescription())
                        .build());
            } catch (Exception e) {
                System.err.println("주식 가격 업데이트 중 오류 발생: " + e.getMessage());
                throw new IllegalArgumentException("주식 가격 업데이트 중 오류 발생: " + e.getMessage(), e);
            }
        }

        return stockDTOList;
    }



    // 개별 주식 가격 업데이트 (폐장 시 호출)
    private StockDTO updateStockPrice(Stock stock) {
        // 폐장 시점(평일 15시 이후)에, 전날의 매수·매도량 조회
        LocalDate yesterday = LocalDate.now().minusDays(1);
        int dailyBuyVolume = stockTransactionRepository.getBuyVolumeForStockYesterday(stock.getId(), TransType.BUY, yesterday);
        int dailySellVolume = stockTransactionRepository.getSellVolumeForStockYesterday(stock.getId(), TransType.SELL, yesterday);

        // 이전 가격 저장
        double oldPrice = stock.getPrice_per();

        // 거래량이 없는 경우 변동 없음
        if (dailyBuyVolume == 0 && dailySellVolume == 0) {
            System.out.println("거래량 없음, 가격 유지: " + stock.getName());
            stock.setChangeRate(0);
        } else {
            // 매수량, 매도량에 따른 새로운 가격 계산
            double newPrice = calculatePriceChangeBasedOnTransaction(dailyBuyVolume, dailySellVolume, stock.getPrice_per());
            // 가격 변동 적용
            stock.setPrice_per((int) Math.round(newPrice));
            stock.setPriceChangeTime(LocalDateTime.now());
            System.out.println("가격 변동 적용: " + stock.getName());

            // 변동률 계산 및 적용
            double changeRate = calculateChangeRate(oldPrice, newPrice);
            stock.setChangeRate((int) (Math.round(changeRate * 100) / 100.0));
        }

        // 가격 변동 이력 저장
        StockHistory history = new StockHistory();
        history.setStock(stock);
        history.setPrice(stock.getPrice_per());
        history.setBuyVolume(dailyBuyVolume);
        history.setSellVolume(dailySellVolume);
        history.setDate(Timestamp.valueOf(LocalDateTime.now().minusDays(1)));
        stockHistoryRepository.save(history);

        // 주식 정보 저장
        stockRepository.save(stock);
        System.out.println("주식 가격 업데이트 완료: " + stock.getName());

        // 업데이트된 Stock 정보를 DTO로 변환하여 반환
        return StockDTO.fromEntity(stock);
    }



    // 변동률 계산 로직: (신가격 - 이전가격) / 이전가격 * 100
    public double calculateChangeRate(double oldPrice, double newPrice) {
        if (oldPrice == 0) return 0;
        return (newPrice - oldPrice) / oldPrice * 100;
    }

    // 매수량, 매도량에 따른 가격 변동 계산 (초등학생용 간단 계산)
    // (매수량 - 매도량) / (매수량 + 매도량) 결과를 -10% ~ +10%로 제한하여 적용
    public double calculatePriceChangeBasedOnTransaction(int buyVolume, int sellVolume, double currentPrice) {
        if (buyVolume == 0 && sellVolume == 0) {
            return currentPrice;
        }

        try {
            double changeRate = (double) (buyVolume - sellVolume) / (buyVolume + sellVolume);
            // 변동률을 ±10%로 제한
            changeRate = Math.max(-0.10, Math.min(0.10, changeRate));
            return currentPrice * (1 + changeRate);
        } catch (ArithmeticException e) {
            throw new IllegalArgumentException("매수량과 매도량의 합이 0입니다. 가격 계산이 불가능합니다.");
        }
    }



    // 주식시장 활성화 여부 조회 (선생님이 설정한 값)
    public boolean isMarketActive() {
        Stock stock = stockRepository.findById(1L).orElseThrow(() -> new RuntimeException("Stock not found"));
        return stock.getIsMarketActive();  // 선생님이 설정한 값
    }

    //주식시장 활성화/비활성화 설정 (선생님이 버튼으로 설정)
    @Transactional
    public void setMarketActive(boolean isActive) {
        Stock stock = stockRepository.findById(1L).orElseThrow(() -> new RuntimeException("Stock not found"));
        stock.setIsMarketActive(isActive);
        stockRepository.save(stock);
    }

    // 현재 주식 거래 가능 여부 조회 (시장 활성화 + 시간 체크)
    public boolean isTradingAllowed() {
        Stock stock = stockRepository.findById(1L).orElseThrow(() -> new RuntimeException("Stock not found"));

        // 시장이 비활성화 상태면 시간과 상관없이 거래 불가
        if (!stock.getIsMarketActive()) {
            return false;
        }

        // 거래 가능 시간: 9시 ~ 17시
        LocalTime currentTime = LocalTime.now();
        return !currentTime.isBefore(LocalTime.of(9, 0)) && !currentTime.isAfter(LocalTime.of(17, 0));
    }
}