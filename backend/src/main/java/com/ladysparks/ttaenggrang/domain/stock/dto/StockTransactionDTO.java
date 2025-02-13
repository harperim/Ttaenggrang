package com.ladysparks.ttaenggrang.domain.stock.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ladysparks.ttaenggrang.domain.stock.entity.StockTransaction;
import com.ladysparks.ttaenggrang.domain.stock.entity.TransType;
import lombok.*;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockTransactionDTO {
    private Long id;
    private int share_count;  // 주식 거래 수량
    private Timestamp trans_date;  // 거래 날짜
    private int purchase_prc;   // 거래 당시 1주 가격
    private int total_amt;  // 총 거래 금액
//    private int return_amt;   // 현재 주가
    @JsonIgnore  //  응답에서 숨김
    private BigDecimal returnRate;  // 손익/손실 금액
    private TransType transType; // 거래 유형
    private int owned_qty;      // 학생이 보유한 주식 수량

    //조인

    // 학생 관련 (student_id 외래 키를 참조)
    private Long studentId;  // student_id 외래 키 (Student 엔티티 참조)


    // 주식 관련 (stock_id 외래 키를 참조)
    private Long stockId;     // stock_id 외래 키 (Stock 엔티티 참조)

    public static StockTransactionDTO fromEntity(StockTransaction stockTransaction, int updatedOwnedQty) {
        return StockTransactionDTO.builder()
                .id(stockTransaction.getId())
                .share_count(stockTransaction.getShare_count())
                .trans_date(stockTransaction.getTrans_date())
                .purchase_prc(stockTransaction.getPurchase_prc())  // 거래 당시 1주 가격
                .total_amt(stockTransaction.getTotal_amt())  // 총 거래 금액
//                .return_amt(stockTransaction.getReturn_amt())  // 현재 주가
                .returnRate(stockTransaction.getReturnRate())  // 손익/손실 금액
                .transType(stockTransaction.getTransType())  // 거래 유형
                .owned_qty(stockTransaction.getOwned_qty())   // 학생이 보유한 주식 수량
                .studentId(stockTransaction.getStudent() != null ? stockTransaction.getStudent().getId() : null)  // 조인된 Student 엔티티에서 ID 가져오기
//                .bankAccountId(stockTransaction.getBankAccount() != null ? stockTransaction.getBankAccount().getId() : null)  // 조인된 BankAccount 엔티티에서 ID 가져오기
                // stockId를 Long으로 처리할 수 있도록 수정
                .stockId(stockTransaction.getStock() != null ? Long.valueOf(stockTransaction.getStock().getId()) : null)

                .build();// 조인된 Stock 엔티티에서 ID 가져오기

        }


}
