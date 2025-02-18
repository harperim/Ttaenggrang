package com.ladysparks.ttaenggrang.domain.etf.service;

import com.ladysparks.ttaenggrang.domain.bank.dto.BankTransactionDTO;
import com.ladysparks.ttaenggrang.domain.bank.entity.BankTransaction;
import com.ladysparks.ttaenggrang.domain.bank.service.BankTransactionService;
import com.ladysparks.ttaenggrang.domain.etf.dto.EtfDTO;
import com.ladysparks.ttaenggrang.domain.etf.dto.EtfTransactionDTO;
import com.ladysparks.ttaenggrang.domain.etf.dto.EtfTransactionResponseDTO;
import com.ladysparks.ttaenggrang.domain.etf.entity.Etf;
import com.ladysparks.ttaenggrang.domain.etf.entity.EtfTransaction;
import com.ladysparks.ttaenggrang.domain.etf.entity.TransType;
import com.ladysparks.ttaenggrang.domain.etf.repository.EtfRepository;
import com.ladysparks.ttaenggrang.domain.etf.repository.EtfTransactionRepository;
import com.ladysparks.ttaenggrang.domain.stock.dto.StockDTO;
import com.ladysparks.ttaenggrang.domain.stock.dto.StockTransactionResponseDTO;
import com.ladysparks.ttaenggrang.domain.stock.entity.Stock;
import com.ladysparks.ttaenggrang.domain.stock.entity.StockTransaction;
import com.ladysparks.ttaenggrang.domain.stock.entity.TransactionType;
import com.ladysparks.ttaenggrang.domain.student.entity.Student;
import com.ladysparks.ttaenggrang.domain.student.repository.StudentRepository;
import com.ladysparks.ttaenggrang.domain.teacher.dto.StudentEtfTransactionDTO;
import com.ladysparks.ttaenggrang.domain.teacher.dto.StudentStockTransactionDTO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class EtfTransactionService {
    private final EtfRepository etfRepository;
    private final EtfTransactionRepository etfTransactionRepository;
    private final StudentRepository studentRepository;
    private final BankTransactionService bankTransactionService;
    private final EtfService etfService;

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
        transactionRequest.setType(BankTransaction.BankTransactionType.ETF_BUY);
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
        transaction.setTransDate(new Timestamp(System.currentTimeMillis()));  //날짜
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
        transactionRequest.setType(BankTransaction.BankTransactionType.ETF_SELL);
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
        transaction.setTransDate(new Timestamp(System.currentTimeMillis())); // 거래 날짜
        transaction.setOwned_qty(updatedOwnedQty); // 매도 후 남은 보유량 저장
        transaction.setTotal_amt(totalAmount);
        transaction.setPurchase_prc(price_per); // 현재 가격 저장

        // 매도 거래 저장
        etfTransactionRepository.save(transaction);

        // DTO 변환 후 반환
        return EtfTransactionDTO.fromEntity(transaction, updatedOwnedQty);
    }


    // 학생의 거래 내역 조회 (매수 + 매도)
    public List<EtfTransactionResponseDTO> getStudentTransactions(Long studentId) {
        // 학생 ID를 기준으로 모든 거래 내역을 조회
        List<EtfTransaction> transactions = etfTransactionRepository.findByStudentId(studentId);
        return convertToTransactionDTO(transactions);  // DTO로 변환하여 반환
    }

    // StockTransaction을 TransactionDTO로 변환
    private List<EtfTransactionResponseDTO> convertToTransactionDTO(List<EtfTransaction> transactions) {
        List<EtfTransactionResponseDTO> transactionDTOList = new ArrayList<>();
        for (EtfTransaction transaction : transactions) {
            EtfTransactionResponseDTO transactionDTO = new EtfTransactionResponseDTO();

            // 학생 ID와 관련된 정보 설정
            transactionDTO.setStudentId(transaction.getStudent().getId());

            // 주식 관련 정보 설정 (name과 type만 가져오기)
            Etf etf = transaction.getEtf();
            transactionDTO.setEtfId(etf.getId());
            transactionDTO.setName(etf.getName());  // 주식명
            transactionDTO.setType(etf.getType());  // 주식 타입
            transactionDTO.setCurrentPrice(etf.getPrice_per()); // 현재 가격

            // 거래 정보 설정
            transactionDTO.setTransType(transaction.getTransType());
            transactionDTO.setShare_count(transaction.getShare_count());
            transactionDTO.setPurchase_prc(transaction.getPurchase_prc());  // 1주 가격
            transactionDTO.setTransDate(transaction.getTransDate()); // 거래 날짜

            // DTO 리스트에 추가
            transactionDTOList.add(transactionDTO);
        }

        return transactionDTOList;
    }

    public List<StudentEtfTransactionDTO> findStudentEtfTransactionsByStudentId(Long studentId) {
        List<EtfTransactionResponseDTO> etfTransactionResponseDTOList = getStudentTransactions(studentId);
        Map<Long, StudentEtfTransactionDTO> etfSummaryMap = new HashMap<>();

        for (EtfTransactionResponseDTO transaction : etfTransactionResponseDTOList) {
            EtfDTO etfDTO = etfService.findEtf(transaction.getEtfId()).orElseGet(EtfDTO::new);
            Long etfId = transaction.getEtfId();

            StudentEtfTransactionDTO etfSummary = etfSummaryMap.getOrDefault(etfId,
                    StudentEtfTransactionDTO.builder()
                            .etfId(etfId)
                            .etfName(etfDTO.getName())
                            .quantity(0)
                            .currentTotalPrice(0)
                            .purchasePrice(0)
                            .priceChangeRate(0)
                            .build()
            );

            int prevQuantity = etfSummary.getQuantity();
            int prevTotalPurchasePrice = etfSummary.getPurchasePrice() * prevQuantity;

            // 매수 (BUY) 처리
            if (transaction.getTransType() == TransType.BUY) {
                int newQuantity = prevQuantity + transaction.getShare_count();
                int totalPurchasePrice = prevTotalPurchasePrice + (transaction.getPurchase_prc() * transaction.getShare_count());
                int newAveragePurchasePrice = (newQuantity > 0) ? totalPurchasePrice / newQuantity : 0;

                etfSummary.setPurchasePrice(newAveragePurchasePrice);
                etfSummary.setQuantity(newQuantity);
            }

            // 매도 (SELL) 처리
            else if (transaction.getTransType() == TransType.SELL) {
                int newQuantity = prevQuantity - transaction.getShare_count();
                etfSummary.setQuantity(Math.max(newQuantity, 0)); // 음수가 되지 않도록 조정
            }

            // 현재 총 평가 금액 = 현재 주가 * 보유 수량
            int currentPrice = etfDTO.getPrice_per();
            etfSummary.setCurrentTotalPrice(etfSummary.getQuantity() > 0 ? currentPrice * etfSummary.getQuantity() : 0);

            // 변동률 계산
            if (etfSummary.getPurchasePrice() == 0 || etfSummary.getQuantity() == 0) {
                etfSummary.setPriceChangeRate(0);
            } else {
                int priceChangeRate = Math.round(((float) (currentPrice - etfSummary.getPurchasePrice()) / etfSummary.getPurchasePrice()) * 100);
                etfSummary.setPriceChangeRate(priceChangeRate);
            }

            etfSummaryMap.put(etfId, etfSummary);
        }

        return new ArrayList<>(etfSummaryMap.values());
    }

    public int getTotalBuyVolume(Long etfId, LocalDateTime startOfDay, LocalDateTime endOfDay) {
        return etfTransactionRepository.getTotalBuyVolume(etfId, startOfDay, endOfDay);
    }

    public int getTotalSellVolume(Long etfId, LocalDateTime startOfDay, LocalDateTime endOfDay) {
        return etfTransactionRepository.getTotalSellVolume(etfId, startOfDay, endOfDay);
    }





}
