package com.ladysparks.ttaenggrang.domain.etf.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ladysparks.ttaenggrang.domain.etf.entity.EtfTransaction;
import com.ladysparks.ttaenggrang.domain.etf.entity.TransType;
import com.ladysparks.ttaenggrang.domain.stock.entity.TransactionType;
import lombok.*;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EtfTransactionDTO {
    private Long id;
    private int share_count;    // ETF 거래 수량
    private Timestamp transDate; // 거래 날짜
    private int purchase_prc; // 거래 당시 1주 가격
    private int total_amt;  // 총 거래 금액
    @JsonIgnore  //  응답에서 숨김
    private BigDecimal returnRate;  // 손익/손실 금액
    private TransType transType; // 거래 유형
    private int owned_qty;      // 학생이 보유한 주식 수량

    //조인
    // ETF 관련 (ETF ID를 참조)
    private Long etfId;          // ETF ID

    // 학생 관련 (학생 ID를 참조)
    private Long studentId;      // 학생 ID

    // 계좌 관련 (bank_account_id 외래 키를 참조)
    @JsonIgnore  // 응답에서 숨김
    private Long bankAccountId;

    public static EtfTransactionDTO fromEntity(EtfTransaction etfTransaction, int updatedOwnedQty) {
        return EtfTransactionDTO.builder()
                .id(etfTransaction.getId())
                .share_count(etfTransaction.getShare_count())
                .transDate(etfTransaction.getTransDate())
                .purchase_prc(etfTransaction.getPurchase_prc())  // 거래 당시 1주 가격
                .total_amt(etfTransaction.getTotal_amt())  // 총 거래 금액
                .returnRate(etfTransaction.getReturnRate())  // 손익/손실 금액
                .transType(etfTransaction.getTransType())  // 거래 유형
                .owned_qty(etfTransaction.getOwned_qty())   // 학생이 보유한 주식 수량
                .studentId(etfTransaction.getStudent() != null ? etfTransaction.getStudent().getId() : null)  // 조인된 Student 엔티티에서 ID 가져오기
//                .bankAccountId(stockTransaction.getBankAccount() != null ? stockTransaction.getBankAccount().getId() : null)  // 조인된 BankAccount 엔티티에서 ID 가져오기
                .etfId(Long.valueOf(etfTransaction.getEtf() != null ? etfTransaction.getEtf().getId() : null))
                .build();// 조인된 Stock 엔티티에서 ID 가져오기

    }

}
