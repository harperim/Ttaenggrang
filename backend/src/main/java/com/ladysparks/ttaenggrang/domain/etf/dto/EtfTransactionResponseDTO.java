package com.ladysparks.ttaenggrang.domain.etf.dto;

import com.ladysparks.ttaenggrang.domain.etf.entity.TransType;
import com.ladysparks.ttaenggrang.domain.stock.entity.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EtfTransactionResponseDTO {
    private Long studentId;                  // 학생 ID
    private Long etfId;                    // 거래된 주식 ID
    private String name;                     // 주식명
    private String type;                     // 주식 타입
    private TransType transType; // 거래 타입(BUY/SELL)
    private int share_count;                  // 주식 거래 수량
    private int currentPrice;                // 현재 주식 가격
    private Timestamp transDate;       // 거래 날짜
    private int purchase_prc;       // 거래 당시 1주 가격
}
