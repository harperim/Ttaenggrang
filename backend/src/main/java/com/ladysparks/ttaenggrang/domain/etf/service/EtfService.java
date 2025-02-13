package com.ladysparks.ttaenggrang.domain.etf.service;

import com.ladysparks.ttaenggrang.domain.bank.dto.BankTransactionDTO;
import com.ladysparks.ttaenggrang.domain.bank.entity.BankTransaction.BankTransactionType;
import com.ladysparks.ttaenggrang.domain.bank.service.BankTransactionService;
import com.ladysparks.ttaenggrang.domain.etf.dto.EtfTransactionDTO;
import com.ladysparks.ttaenggrang.domain.etf.entity.Etf;
import com.ladysparks.ttaenggrang.domain.etf.dto.EtfDTO;
import com.ladysparks.ttaenggrang.domain.etf.entity.EtfTransaction;
import com.ladysparks.ttaenggrang.domain.etf.repository.EtfRepository;
import com.ladysparks.ttaenggrang.domain.etf.repository.EtfTransactionRepository;
import com.ladysparks.ttaenggrang.domain.stock.entity.Stock;
import com.ladysparks.ttaenggrang.domain.stock.entity.StockTransaction;
import com.ladysparks.ttaenggrang.domain.stock.entity.TransType;
import com.ladysparks.ttaenggrang.domain.stock.repository.StockHistoryRepository;
import com.ladysparks.ttaenggrang.domain.stock.repository.StockRepository;
import com.ladysparks.ttaenggrang.domain.stock.repository.StockTransactionRepository;
import com.ladysparks.ttaenggrang.domain.student.entity.Student;
import com.ladysparks.ttaenggrang.domain.student.repository.StudentRepository;
import jakarta.transaction.Transaction;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
@Service
@RequiredArgsConstructor
public class EtfService {//s
    private final EtfRepository etfRepository; //의존성 주입

    private final EtfTransactionRepository etfTransactionRepository;
    //학생
    private final StudentRepository studentRepository;

    private final StockHistoryRepository stockHistoryRepository;

    private final BankTransactionService bankTransactionService;

    private final StockRepository stockRepository;

    private final StockTransactionRepository stockTransactionRepository;




//    //ETF 생성
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

    public  Long saveEtf(EtfDTO etfDto) {
        // EtfDTO를 Etf 엔티티로 변환
        Etf etf = EtfDTO.toEntity(etfDto);
        // 변환된 엔티티를 DB에 저장
        etfRepository.save(etf);
        // 저장된 엔티티의 ID 반환
        return etf.getId();
    }

    public List<EtfDTO> findEtfs() {
        //모든 주식 데이터 조회
        List<Etf> etfs = etfRepository.findAll();
        // 조회된 ETF 엔티티 리스트를 ETFDTO 리스트로 변환
        return etfs.stream()
                .map(EtfDTO::fromEntity) // 엔티티를 DTO로 변환
                .collect(Collectors.toList()); // 변환된 DTO를 리스트로 반환
    }

    public Optional<EtfDTO> findEtf(int etfId) {
        // ID로 주식 조회 후, ETFDTO로 변환하여 반환
        return etfRepository.findById(etfId)
                .map(EtfDTO::fromEntity); // 엔티티를 DTO로 변환
    }

    // ETF 매수 로직
    @Transactional
    public EtfTransactionDTO buyEtf(int etfId, int shareCount, Long studentId) {
        // 주식 정보 가져오기
        Optional<Etf> etfOptional = etfRepository.findById(etfId);
        if (etfOptional.isEmpty()) {
            throw new IllegalArgumentException("주식이 존재하지 않습니다.");
        }
        Etf etf = etfOptional.get();

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
        if (etf.getRemain_qty() < shareCount) {
            throw new IllegalArgumentException("남은 수량이 부족합니다.");
        }


        // 주식 현재 가격 가져오기
        int price_per = etf.getPrice_per();
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
        transactionRequest.setType(BankTransactionType.ETF_BUY);
        transactionRequest.setAmount(totalAmount);
        transactionRequest.setDescription("ETF 매수: " + etf.getName());

        BankTransactionDTO bankTransactionDTO = bankTransactionService.addBankTransaction(transactionRequest);


        // 은행 서비스에서 받은 최종 잔액 확인
        int balanceAfter = bankTransactionDTO.getBalanceAfter();
        System.out.println("ETF 매수 완료, 남은 잔액: " + balanceAfter);


        // 주식의 재고 수량 차감
        etf.setRemain_qty(etf.getRemain_qty() - shareCount);
        etfRepository.save(etf);


        // 학생이 현재 보유한 해당 주식 수량 조회
        Integer owned_qty = etfTransactionRepository.findTotalSharesByStudentAndEtf(studentId, etfId, TransType.BUY);
        if (owned_qty == null) {
            owned_qty = 0; // 처음 구매라면 0으로 설정
        }

        // 기존 보유량 + 새로 매수한 수량
        int updatedOwnedQty = owned_qty + shareCount;


        // 새로운 매수 거래 생성
        EtfTransaction transaction = new EtfTransaction();
        transaction.setEtf(etf);
        transaction.setStudent(student);
        transaction.setShare_count(shareCount);
        transaction.setTransType(TransType.BUY);
        transaction.setTrans_date(new Timestamp(System.currentTimeMillis()));  //날짜
        transaction.setOwned_qty(updatedOwnedQty); // 기존 보유량 + 새로 매수한 수량
        transaction.setTotal_amt(totalAmount);
        transaction.setPurchase_prc(price_per); // 현재 가격을 그대로 저장

        etfTransactionRepository.save(transaction);

        // 주식의 현재 가격을 업데이트
        etf.setPrice_per(price_per);
        etfRepository.save(etf);

        return EtfTransactionDTO.fromEntity(transaction, updatedOwnedQty);
    }

    //ETF 매도
    @Transactional
    public EtfTransactionDTO sellEtf(int etfId, int shareCount, Long studentId) {
        // 주식 정보 가져오기
        Etf etf = etfRepository.findById(etfId)
                .orElseThrow(() -> new IllegalArgumentException("주식이 존재하지 않습니다."));

        // 학생 정보 가져오기
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("학생이 존재하지 않습니다."));

        // 매도 가능한 수량 확인
        if (shareCount <= 0) {
            throw new IllegalArgumentException("0 이하 수량은 매도할 수 없습니다.");
        }

        // 🟢 (수정) 학생의 총 매수량(BUY)과 총 매도량(SELL) 조회
        Integer totalBought = etfTransactionRepository.findTotalSharesByStudentAndEtf(studentId, etfId, TransType.BUY);
        Integer totalSold = etfTransactionRepository.findTotalSharesByStudentAndEtf(studentId, etfId, TransType.SELL);

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
        int price_per = etf.getPrice_per();
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
        transactionRequest.setType(BankTransactionType.ETF_SELL);
        transactionRequest.setAmount(totalAmount);
        transactionRequest.setDescription("ETF 매도: " + etf.getName());

        BankTransactionDTO bankTransactionDTO = bankTransactionService.addBankTransaction(transactionRequest);


        // 은행 서비스에서 받은 최종 잔액 확인
        int balanceAfter = bankTransactionDTO.getBalanceAfter();
        System.out.println("ETF 매도 완료, 남은 잔액: " + balanceAfter);


        etf.setRemain_qty(etf.getRemain_qty() + shareCount);
        etfRepository.save(etf);


        int updatedOwnedQty = owned_qty - shareCount;


        EtfTransaction transaction = new EtfTransaction();
        transaction.setEtf(etf);
        transaction.setStudent(student);
        transaction.setShare_count(shareCount);
        transaction.setTransType(TransType.SELL); // 매도 타입
        transaction.setTrans_date(new Timestamp(System.currentTimeMillis())); // 거래 날짜
        transaction.setOwned_qty(updatedOwnedQty); // 매도 후 남은 보유량 저장
        transaction.setTotal_amt(totalAmount);
        transaction.setPurchase_prc(price_per); // 현재 가격 저장

        // 매도 거래 저장
        etfTransactionRepository.save(transaction);

        // DTO 변환 후 반환
        return EtfTransactionDTO.fromEntity(transaction, updatedOwnedQty);
    }

//    //가격 변동
//    @Transactional
//    public EtfDTO updateEtfPrice(int etfId) {
//        Etf etf = etfRepository.findById(etfId)
//                .orElseThrow(() -> new RuntimeException("주식이 존재하지 않습니다."));
//
//        // 주식장이 활성화된 경우에만 가격 변동이 가능
////        if (!etf.isMarketActive()) {
////            throw new RuntimeException("주식장이 활성화되지 않았습니다.");
////        }
//
//        // 전날 날짜 계산
//        LocalDate yesterday = LocalDate.now().minusDays(1);
//
//        // LocalDate를 Timestamp로 변환 (00:00:00로 설정)
//        Timestamp startTimestamp = Timestamp.valueOf(yesterday.atStartOfDay());
//        // LocalDate를 Timestamp로 변환 (23:59:59로 설정)
//        Timestamp endTimestamp = Timestamp.valueOf(yesterday.atTime(23, 59, 59));
//
//        // 전날 매수, 매도 수량 가져오기 (날짜 범위 추가)
//        int totalBought = etfTransactionRepository.getTotalSharesByType(etfId, TransType.BUY, startTimestamp, endTimestamp);
//        int totalSold = etfTransactionRepository.getTotalSharesByType(etfId, TransType.SELL, startTimestamp, endTimestamp);
//
//        // 매수, 매도 수량 평균 계산
//        int totalTransactions = totalBought + totalSold;
//        double calculatedChangeRate = 0.0;
//
//        if (totalTransactions > 0) {
//            double buyRatio = (double) totalBought / totalTransactions;
//            double sellRatio = (double) totalSold / totalTransactions;
//            calculatedChangeRate = (buyRatio - sellRatio) * 0.05; // 최대 ±5% 변동
//        }
//
//        // 새로운 가격 계산
//        int currentPrice = etf.getPrice_per();
//        int newPrice = (int) (currentPrice * (1 + calculatedChangeRate));
//
//        // 최소 가격 제한
//        if (newPrice < 1000) {
//            newPrice = 1000;
//        }
//
//        // 가격 업데이트
//        etf.setPrice_per(newPrice);
//        etf.setChangeRate((int) (calculatedChangeRate * 100));
//        etfRepository.save(etf);
//
//        System.out.println(etf.getName() + "의 새 가격: " + newPrice);
//
//        // 변동된 가격을 stock_history 테이블에 기록
//        StockHistory history = new StockHistory();
//        history.setEtf(etf);
//        history.setPrice(newPrice);
//        history.setVolume(totalTransactions);
//        history.setDate(Timestamp.valueOf(LocalDateTime.now()));
//        stockHistoryRepository.save(history);
//
//        // DTO 변환 및 반환
//        return EtfDTO.fromEntity(etf);
//    }

//    public EtfDTO createETF(EtfDTO etfDTO) {
//        String name = etfDTO.getName();  // ETF 이름
//        String description = etfDTO.getDescription();  // 설명
//        List<Long> selectedStockIds = etfDTO.getStock_id();  // 선택된 주식 ID 리스트
//        int total_qty = etfDTO.getTotal_qty();  // ETF 총 수량 (선생님이 정함)
//
//        // ETF 이름 중복 검사
//        if (etfRepository.existsByName(name)) {
//            throw new IllegalArgumentException("이미 존재하는 ETF 이름입니다: " + name);
//        }
//
//        // 선택한 주식 개수 검증 (최소 3개 이상 선택해야 함)
//        if (selectedStockIds == null || selectedStockIds.size() < 3) {
//            throw new IllegalArgumentException("ETF는 최소 3개 이상의 주식을 선택해야 합니다.");
//        }
//
//        // ETF 총 수량이 100 미만일 경우 예외 처리
//        if (total_qty < 100) {
//            throw new IllegalArgumentException("ETF 총 수량은 최소 100개 이상이어야 합니다.");
//        }
//
//        // 선택된 주식 정보만 필터링
//        List<StockDTO> availableStocks = stockService.getFilteredStocks(selectedStockIds);
//
//        // 주식 리스트가 비어있는 경우 예외 처리
//        if (availableStocks.isEmpty()) {
//            throw new IllegalArgumentException("선택된 주식이 없습니다.");
//        }
//
//        // 주식들의 카테고리 추출 (모든 주식의 카테고리가 동일하다고 가정)
//        String category = availableStocks.get(0).getCategory();  // 선택된 주식의 카테고리 정보
//
//        // 주식 개수에 맞게 비중 계산 (예: 4개의 주식이라면 각 주식 비중은 25%)
//        BigDecimal stockCount = BigDecimal.valueOf(availableStocks.size());
//        BigDecimal individualWeight = BigDecimal.valueOf(100).divide(stockCount, RoundingMode.HALF_UP); // 100% / 선택한 주식 개수
//
//        // 주식의 총 가격 계산
//        BigDecimal totalPrice = BigDecimal.ZERO;
//
//        for (StockDTO stockDTO : availableStocks) {
//            // 주식 가격과 비중 계산 (각 주식에 대해 동일한 비중을 적용)
//            totalPrice = totalPrice.add(BigDecimal.valueOf(stockDTO.getPrice_per()));
//        }
//
//        // ETF 한 주당 가격 계산
//        BigDecimal price_per = totalPrice.divide(stockCount, RoundingMode.HALF_UP);
//
//        // 남은 수량 설정: 처음에는 총 수량과 동일
//        int remain_qty = total_qty;
//
//        // ETF 객체 생성
//        Etf etf = new Etf();
//        etf.setName(name);
//        etf.setDescription(description);
//        etf.setPrice_per(price_per.intValue());  // 한 주당 가격
//        etf.setTotal_qty(total_qty);  // 총 수량
//        etf.setRemain_qty(remain_qty);  // 남은 수량
//        etf.setCreated_at(new Timestamp(System.currentTimeMillis()));  // 생성일
//        etf.setUpdated_at(new Timestamp(System.currentTimeMillis()));  // 수정일
//        etf.setChangeRate(0);  // 변동률 (기본값 0, 실제 가격 변동에 따라 업데이트 필요)
//
//        // 주식에 대해 비중 설정 (각 주식은 동일 비중)
//        for (StockDTO stockDTO : availableStocks) {
//            BigDecimal stockWeight = individualWeight;  // 각 주식의 비중은 동일
//            stockDTO.setWeight(stockWeight);  // 주식의 비중을 설정
//        }
//
//        // ETF 저장
//        etfRepository.save(etf);
//
//        // DTO로 변환하여 반환
//        return EtfDTO.fromEntity(etf);
//    }


}