package com.ladysparks.ttaenggrang.domain.stock.dto;

import com.ladysparks.ttaenggrang.domain.stock.entity.StockTransaction;
import com.ladysparks.ttaenggrang.domain.stock.entity.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockTransactionDTO {

    private Long id;
    private int shareQuantity;                  // 주식 거래 수량
    private Timestamp transactionDate;          // 거래 날짜
    private int purchasePerShare;               // 거래 당시 1주 가격
    private Integer totalPrice;                 // 총 거래 금액
    private BigDecimal returnRate;              // 손익/손실 금액(매도일 때만)
    private TransactionType transactionType;    // 거래 유형
    private int totalQuantity;                  // 학생이 보유한 주식 수량
    private StockDTO stockDTO;

    // 조인
    // 학생 관련
    private Long studentId;  // student_id 외래 키

    // 주식 관련
    private Long stockId;     // stock_id 외래 키

    public static StockTransactionDTO fromEntity(StockTransaction stockTransaction, int updatedOwnedQty) {
        return StockTransactionDTO.builder()
                .id(stockTransaction.getId())
                .shareQuantity(stockTransaction.getShare_count())
                .transactionDate(stockTransaction.getTrans_date())
                .purchasePerShare(stockTransaction.getPurchase_prc())  // 거래 당시 1주 가격
                .totalPrice(stockTransaction.getTotal_amt())  // 총 거래 금액
                .returnRate(stockTransaction.getReturnRate())  // 손익/손실 금액
                .transactionType(stockTransaction.getTransactionType())  // 거래 유형
                .totalQuantity(stockTransaction.getOwned_qty())   // 학생이 보유한 주식 수량
                .studentId(stockTransaction.getStudent() != null ? stockTransaction.getStudent().getId() : null)  // 조인된 Student 엔티티에서 ID 가져오기
                // stockId를 Long으로 처리할 수 있도록 수정
                .stockId(stockTransaction.getStock() != null ? Long.valueOf(stockTransaction.getStock().getId()) : null)
                .build();// 조인된 Stock 엔티티에서 ID 가져오기
        }

}
