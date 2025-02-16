package com.ladysparks.ttaenggrang.domain.stock.service;

import com.ladysparks.ttaenggrang.domain.bank.dto.BankTransactionDTO;
import com.ladysparks.ttaenggrang.domain.bank.entity.BankTransaction.BankTransactionType;
import com.ladysparks.ttaenggrang.domain.bank.service.BankTransactionService;
import com.ladysparks.ttaenggrang.domain.stock.dto.StockDTO;
import com.ladysparks.ttaenggrang.domain.stock.dto.StockTransactionDTO;
import com.ladysparks.ttaenggrang.domain.stock.dto.StockTransactionResponseDTO;
import com.ladysparks.ttaenggrang.domain.stock.entity.Stock;
import com.ladysparks.ttaenggrang.domain.stock.entity.StockTransaction;
import com.ladysparks.ttaenggrang.domain.stock.entity.TransactionType;
import com.ladysparks.ttaenggrang.domain.stock.repository.StockRepository;
import com.ladysparks.ttaenggrang.domain.stock.repository.StockTransactionRepository;
import com.ladysparks.ttaenggrang.domain.student.entity.Student;
import com.ladysparks.ttaenggrang.domain.student.repository.StudentRepository;
import com.ladysparks.ttaenggrang.domain.teacher.dto.StudentStockTransactionDTO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StockTransactionService {

    private final StockRepository stockRepository;
    private final StockTransactionRepository stockTransactionRepository;
    private final StudentRepository studentRepository;
    private final BankTransactionService bankTransactionService;
    private final StockService stockService;

    // 주식 매수 로직
    @Transactional
    public StockTransactionDTO buyStock(Long stockId, int shareCount, Long studentId) {
        // 거래 가능 여부 판단
//        Long teacherId = studentService.findTeacherIdByStudentId(studentId);
//        if (!stockMarketStatusService.isMarketOpen(teacherId)) {
//            throw new IllegalArgumentException("지금은 거래 가능 시간이 아닙니다");
//        }

        // 주식 정보 가져오기
        Stock stock = stockRepository.findById(stockId)
                .orElseThrow(() -> new IllegalArgumentException("주식이 존재하지 않습니다."));

        // 학생 정보 가져오기
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("학생이 존재하지 않습니다."));

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

        // 은행 계좌에서 금액 차감 (API 호출)
        Long bankAccountId = student.getBankAccount().getId();
        BankTransactionDTO transactionRequest = new BankTransactionDTO();
        transactionRequest.setBankAccountId(bankAccountId);
        transactionRequest.setType(BankTransactionType.STOCK_BUY);
        transactionRequest.setAmount(totalAmount);
        transactionRequest.setDescription("[주식 매수] " + stock.getName());

        BankTransactionDTO bankTransactionDTO = bankTransactionService.addBankTransaction(transactionRequest);

        // 은행 서비스에서 받은 최종 잔액 확인
        int balanceAfter = bankTransactionDTO.getBalanceAfter();
        System.out.println("주식 매수 완료, 남은 잔액: " + balanceAfter);

        // 주식의 재고 수량 차감
        stock.setRemain_qty(stock.getRemain_qty() - shareCount);
        stockRepository.save(stock);

        // 학생이 현재 보유한 해당 주식 수량 조회
        Integer owned_qty = stockTransactionRepository.findTotalSharesByStudentAndStock(studentId, stockId.intValue(), TransactionType.BUY);
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
        transaction.setTransactionType(TransactionType.BUY);
        transaction.setTransactionDate(new Timestamp(System.currentTimeMillis()));  //날짜
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
        // 거래 가능 여부 판단
//        Long teacherId = studentService.findTeacherIdByStudentId(studentId);
//        if (!stockMarketStatusService.isMarketOpen(teacherId)) {
//            throw new IllegalArgumentException("지금은 거래 가능 시간이 아닙니다");
//        }

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

        // 학생의 총 매수량(BUY)과 총 매도량(SELL) 조회
        Integer totalBought = stockTransactionRepository.findTotalSharesByStudentAndStock(studentId, stockId.intValue(), TransactionType.BUY);
        Integer totalSold = stockTransactionRepository.findTotalSharesByStudentAndStock(studentId, stockId.intValue(), TransactionType.SELL);

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

        // 은행 계좌에서 금액 차감 (API 호출)
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
        transaction.setTransactionType(TransactionType.SELL); // 매도 타입
        transaction.setTransactionDate(new Timestamp(System.currentTimeMillis())); // 거래 날짜
        transaction.setOwned_qty(updatedOwnedQty); // 매도 후 남은 보유량 저장
        transaction.setTotal_amt(totalAmount);
        transaction.setPurchase_prc(price_per); // 현재 가격 저장

        // 매도 거래 저장
        stockTransactionRepository.save(transaction);

        // DTO 변환 후 반환
        return StockTransactionDTO.fromEntity(transaction, updatedOwnedQty);
    }

    // 학생의 거래 내역 조회 (매수 + 매도)
    public List<StockTransactionResponseDTO> getStudentTransactions(Long studentId) {
        // 학생 ID를 기준으로 모든 거래 내역을 조회
        List<StockTransaction> transactions = stockTransactionRepository.findByStudentId(studentId);
        return convertToTransactionDTO(transactions);  // DTO로 변환하여 반환
    }

    // StockTransaction을 TransactionDTO로 변환
    private List<StockTransactionResponseDTO> convertToTransactionDTO(List<StockTransaction> transactions) {
        List<StockTransactionResponseDTO> transactionDTOList = new ArrayList<>();
        for (StockTransaction transaction : transactions) {
            StockTransactionResponseDTO transactionDTO = new StockTransactionResponseDTO();

            // 학생 ID와 관련된 정보 설정
            transactionDTO.setStudentId(transaction.getStudent().getId());

            // 주식 관련 정보 설정 (name과 type만 가져오기)
            Stock stock = transaction.getStock();
            transactionDTO.setStockId(stock.getId());
            transactionDTO.setName(stock.getName());  // 주식명
            transactionDTO.setType(stock.getType());  // 주식 타입
            transactionDTO.setCurrentPrice(stock.getPrice_per()); // 현재 가격

            // 거래 정보 설정
            transactionDTO.setTransactionType(transaction.getTransactionType());
            transactionDTO.setShareCount(transaction.getShare_count());
            transactionDTO.setPurchasePricePerShare(transaction.getPurchase_prc());  // 1주 가격
            transactionDTO.setTransactionDate(transaction.getTransactionDate()); // 거래 날짜

            // DTO 리스트에 추가
            transactionDTOList.add(transactionDTO);
        }

        return transactionDTOList;
    }

    public List<StudentStockTransactionDTO> findStudentStockTransactionsByStudentId(Long studentId) {
        List<StockTransactionResponseDTO> stockTransactionResponseDTOList = getStudentTransactions(studentId);

        // 주식명을 기준으로 그룹화하여 중복 제거
        Map<String, StudentStockTransactionDTO> stockTransactionMap = new HashMap<>();

        for (StockTransactionResponseDTO stockTransactionResponseDTO : stockTransactionResponseDTOList) {
            StockDTO stockDTO = stockService.findStock(stockTransactionResponseDTO.getStockId())
                    .orElseGet(StockDTO::new);

            String stockName = stockDTO.getName();
            int quantity = stockTransactionResponseDTO.getShareCount();
            int purchasePrice = stockTransactionResponseDTO.getPurchasePricePerShare();
            int currentPrice = stockDTO.getPricePerShare();

            // 기존 보유 내역이 있는 경우 가져옴
            StudentStockTransactionDTO existingDTO = stockTransactionMap.getOrDefault(stockName,
                    StudentStockTransactionDTO.builder()
                            .stockName(stockName)
                            .quantity(0)
                            .currentTotalPrice(0)
                            .purchasePrice(0)
                            .priceChangeRate(0)
                            .build());

            int totalQuantity = existingDTO.getQuantity();
            int totalPurchaseAmount = existingDTO.getPurchasePrice() * totalQuantity; // 총 매수 금액

            // 거래 타입에 따라 처리
            if (stockTransactionResponseDTO.getTransactionType() == TransactionType.BUY) {
                // 새로운 총 보유 수량
                totalQuantity += quantity;
                totalPurchaseAmount += purchasePrice * quantity;
            } else if (stockTransactionResponseDTO.getTransactionType() == TransactionType.SELL) {
                // 매도 시 보유 수량 감소 (보유 수량보다 많을 수 없음)
                totalQuantity -= quantity;
                if (totalQuantity < 0) totalQuantity = 0; // 방어 로직
            }

            // 평균 구매 가격 계산 (보유 주식이 없을 경우 0 처리)
            int averagePurchasePrice = (totalQuantity > 0) ? totalPurchaseAmount / totalQuantity : 0;

            // 주가 변동률 계산
            int priceChangeRate = (averagePurchasePrice > 0) ?
                    ((currentPrice - averagePurchasePrice) * 100 / averagePurchasePrice) : 0;

            // DTO 업데이트
            StudentStockTransactionDTO updatedDTO = StudentStockTransactionDTO.builder()
                    .stockName(stockName)
                    .quantity(totalQuantity)
                    .currentTotalPrice(currentPrice * totalQuantity)
                    .purchasePrice(averagePurchasePrice)
                    .priceChangeRate(priceChangeRate)
                    .build();

            stockTransactionMap.put(stockName, updatedDTO);
        }

        // 최종 결과 리스트 반환
        return new ArrayList<>(stockTransactionMap.values());
    }


    public int getTotalBuyVolume(Long stockId, LocalDateTime startOfDay, LocalDateTime endOfDay) {
        return stockTransactionRepository.getTotalBuyVolume(stockId, startOfDay, endOfDay);
    }

    public int getTotalSellVolume(Long id, LocalDateTime startOfDay, LocalDateTime endOfDay) {
        return stockTransactionRepository.getTotalSellVolume(id, startOfDay, endOfDay);
    }
}