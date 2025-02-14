package com.ladysparks.ttaenggrang.domain.stock.service;

import com.ladysparks.ttaenggrang.domain.etf.entity.Etf;
import com.ladysparks.ttaenggrang.domain.etf.repository.EtfRepository;
import com.ladysparks.ttaenggrang.domain.stock.category.Category;
import com.ladysparks.ttaenggrang.domain.stock.category.CategoryRepository;
import com.ladysparks.ttaenggrang.domain.bank.dto.BankTransactionDTO;
import com.ladysparks.ttaenggrang.domain.bank.entity.BankTransaction.BankTransactionType;
import com.ladysparks.ttaenggrang.domain.bank.service.BankTransactionService;
import com.ladysparks.ttaenggrang.domain.stock.dto.*;
import com.ladysparks.ttaenggrang.domain.stock.entity.*;
import com.ladysparks.ttaenggrang.domain.stock.repository.MarketStatusRepository;
import com.ladysparks.ttaenggrang.domain.stock.repository.StockHistoryRepository;
import com.ladysparks.ttaenggrang.domain.stock.repository.StockRepository;
import com.ladysparks.ttaenggrang.domain.stock.repository.StockTransactionRepository;
import com.ladysparks.ttaenggrang.domain.student.entity.Student;
import com.ladysparks.ttaenggrang.domain.student.repository.StudentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.DayOfWeek;
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
    private final EtfRepository etfRepository;
    private final MarketStatusRepository marketStatusRepository;


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

        // 로그로 확인해보세요
        System.out.println("Total Bought: " + totalBought);
        System.out.println("Total Sold: " + totalSold);
        System.out.println("Owned Qty (totalBought - totalSold): " + owned_qty);

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


    // 학생의 거래 내역 조회 (매수 + 매도)
    public List<TransactionResponseDTO> getStudentTransactions(Long studentId) {
        // 학생 ID를 기준으로 모든 거래 내역을 조회
        List<StockTransaction> transactions = stockTransactionRepository.findByStudentId(studentId);

        if (transactions.isEmpty()) {
            // 거래 내역이 없으면 IllegalArgumentException 발생
            throw new IllegalArgumentException("거래 내역을 찾을 수 없습니다. studentId: " + studentId);
        }

        return convertToTransactionDTO(transactions);  // DTO로 변환하여 반환
    }

    // StockTransaction을 TransactionDTO로 변환
    private List<TransactionResponseDTO> convertToTransactionDTO(List<StockTransaction> transactions) {
        List<TransactionResponseDTO> transactionDTOList = new ArrayList<>();
        for (StockTransaction transaction : transactions) {
            TransactionResponseDTO transactionDTO = new TransactionResponseDTO();

            // 학생 ID와 관련된 정보 설정
            transactionDTO.setStudentId(transaction.getStudent().getId());

            // 주식 관련 정보 설정 (name과 type만 가져오기)
            Stock stock = transaction.getStock();
            transactionDTO.setStockId(stock.getId());
            transactionDTO.setName(stock.getName());  // 주식명
            transactionDTO.setType(stock.getType());  // 주식 타입

            // 거래 정보 설정
            transactionDTO.setTransType(transaction.getTransType());
            transactionDTO.setShare_count(transaction.getShare_count());
            transactionDTO.setPurchase_prc(transaction.getPurchase_prc());  // 1주 가격
            transactionDTO.setTrans_date(transaction.getTrans_date()); // 거래 날짜

            // DTO 리스트에 추가
            transactionDTOList.add(transactionDTO);
        }
        return transactionDTOList;
    }
    //학생이 보유 하고 있는 주식 조회
    public List<StudentStockDTO> getStudentStocks(Long studentId) {
        List<StockTransaction> transactions = stockTransactionRepository.findByStudentId(studentId);

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
                        .price_per((int) newPrice)
                        .changeRate((int) changeRate)
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

    // 17시 이후에 내일 9시에 적용될 변동된 가격 계산
    private void updateStockPriceForNextDay(Stock stock) {
        LocalDateTime endTime = LocalDateTime.of(LocalDate.now(), LocalTime.of(17, 0));
        LocalDateTime nextMorning = LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.of(9, 0));

        // 9시부터 17시까지의 거래량 조회
        int dailyBuyVolume = stockTransactionRepository.getBuyVolumeForStockInTimeRange(stock.getId(), TransType.BUY, endTime, nextMorning);
        int dailySellVolume = stockTransactionRepository.getSellVolumeForStockInTimeRange(stock.getId(), TransType.SELL, endTime, nextMorning);

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
        history.setDate(Timestamp.valueOf(LocalDateTime.now()));
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


    // 주식시장 활성화 여부 조회 (선생님이 설정한 값)
    public boolean isMarketActive() {

        LocalDate today = LocalDate.now();
        DayOfWeek dayOfWeek = today.getDayOfWeek();


        // 주말(토, 일)에는 시장 비활성화
        if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) {
            return false;
        }

        // MarketStatus에서 수동 설정(true)된 첫 번째 값을 조회
        MarketStatus marketStatus = marketStatusRepository.findFirstByIsManualOverrideTrue()
                .orElseThrow(() -> new IllegalArgumentException("주식에 대한 시장 상태 정보를 찾을 수 없습니다."));


        // 수동 설정이 있으면 해당 값 반환
        if (marketStatus.isManualOverride()) {
            return marketStatus.isMarketActive();
        }

        // 수동 설정이 없다면 자동으로 설정된 시장 활성화 상태 반환
        return marketStatus.isMarketActive();
    }


    // 주식시장 활성화/비활성화 설정 (선생님이 버튼으로 설정)
    @Transactional
    public void setMarketStatus(boolean isManualOverride) {
        LocalTime currentTime = LocalTime.now();
        boolean isMarketActive;

        if (isManualOverride) {
            isMarketActive = !(currentTime.isAfter(LocalTime.of(17, 0)) || currentTime.isBefore(LocalTime.of(9, 0)));
        } else {
            isMarketActive = false;
        }

        // 주식 상태 업데이트
        List<Stock> stocks = stockRepository.findAll();
        for (Stock stock : stocks) {
            MarketStatus marketStatus = marketStatusRepository.findByStock(stock)
                    .orElse(new MarketStatus());

            marketStatus.setStock(stock);
            marketStatus.setManualOverride(isManualOverride);
            marketStatus.setMarketActive(isMarketActive);

            marketStatusRepository.save(marketStatus);
        }

        // ETF 상태 업데이트
        List<Etf> etfs = etfRepository.findAll();
        for (Etf etf : etfs) {
            MarketStatus marketStatus = marketStatusRepository.findByEtf(etf)
                    .orElse(new MarketStatus());

            marketStatus.setEtf(etf);
            marketStatus.setManualOverride(isManualOverride);
            marketStatus.setMarketActive(isMarketActive);

            marketStatusRepository.save(marketStatus);
        }

        System.out.println("시장 상태가 변경되었습니다. 현재 상태: " +
                (isManualOverride ? "수동 설정 (활성화)" : "자동 설정 (비활성화)"));
    }


    // 현재 주식 거래 가능 여부 조회 (시장 활성화 + 시간 체크)
    public boolean isTradingAllowed() {

        LocalDate today = LocalDate.now();
        DayOfWeek dayOfWeek = today.getDayOfWeek();

        // 주말(토, 일)에는 거래 불가
        if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) {
            return false;
        }

        Stock stock = stockRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException("주식 정보를 찾을 수 없습니다. id=1인 주식이 존재하지 않습니다."));

        MarketStatus marketStatus = marketStatusRepository.findByStock(stock)
                .orElseThrow(() -> new RuntimeException("주식에 대한 시장 상태 정보를 찾을 수 없습니다."));

        // 시장이 비활성화 상태면 거래 불가
        if (!marketStatus.isMarketActive()) {
            return false;
        }

        // 수동 설정이 아니면 거래 불가
        if (!marketStatus.isManualOverride()) {
            return false;
        }

        // 거래 가능 시간: 9시 ~ 17시
        LocalTime currentTime = LocalTime.now();
        return !currentTime.isBefore(LocalTime.of(9, 0)) && !currentTime.isAfter(LocalTime.of(17, 0));
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