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
import com.ladysparks.ttaenggrang.domain.stock.dto.ChangeResponseDTO;
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
import java.util.ArrayList;
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

    public Optional<EtfDTO> findEtf(Long etfId) {
        // ID로 주식 조회 후, ETFDTO로 변환하여 반환
        return etfRepository.findById(etfId)
                .map(EtfDTO::fromEntity); // 엔티티를 DTO로 변환
    }

    // ETF 매수 로직
    @Transactional
    public EtfTransactionDTO buyEtf(Long etfId, int shareCount, Long studentId) {
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
        Integer owned_qty = etfTransactionRepository.findTotalSharesByStudentAndEtf(studentId, etfId.intValue(), TransType.BUY);
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
    public EtfTransactionDTO sellEtf(Long etfId, int shareCount, Long studentId) {
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

        // 학생의 총 매수량(BUY)과 총 매도량(SELL) 조회
        Integer totalBought = etfTransactionRepository.findTotalSharesByStudentAndEtf(studentId, etfId.intValue(), TransType.BUY);
        Integer totalSold = etfTransactionRepository.findTotalSharesByStudentAndEtf(studentId, etfId.intValue(), TransType.SELL);

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
//    public List<ChangeResponseDTO> updateETFPricesForMarketOpening() {
//        List<Etf> etfs = etfRepository.findAll();
//        List<ChangeResponseDTO> etfDTOList = new ArrayList<>();
//
//        for (Etf etf : etfs) {
//            double oldPrice = etf.getPrice_per();
//            try {
//                updateETFPrice(etf);  // ETF 가격 업데이트
//
//                double newPrice = etf.getPrice_per();
//                double changeRate = calculateChangeRate(oldPrice, newPrice);
//
//                etfDTOList.add(ChangeResponseDTO.builder()
//                        .id(etf.getId())
//                        .name(etf.getName())
//                        .price_per((int) newPrice)
//                        .changeRate((int) changeRate)
//                        .isMarketActive(true)
//                        .description(etf.getDescription())
//                        .build());
//
//            } catch (Exception e) {
//                System.err.println("ETF 가격 업데이트 중 오류 발생: " + e.getMessage());
//            }
//        }
//        return etfDTOList;
//    }
////
////    // 개별 ETF 가격 업데이트 (ETF는 기초 지수를 따라 움직인다)
////    private EtfDTO updateETFPrice(Etf etf) {
////        double indexChangeRate = marketIndexService.getIndexChangeRate(etf.getIndexId()); // 지수 변동률
////        double managementFee = 0.001;  // 운용 비용 (예: 0.1%)
////
////        double newPrice = etf.getPrice() * (1 + indexChangeRate - managementFee);
////        etf.setPrice((int) Math.round(newPrice));
////        etf.setPriceChangeTime(LocalDateTime.now());
////
////        etfRepository.save(etf);
////    }
//
//    // 변동률 계산
//    public double calculateChangeRate(double oldPrice, double newPrice) {
//        if (oldPrice == 0) return 0;
//        return (newPrice - oldPrice) / oldPrice * 100;
//    }



}