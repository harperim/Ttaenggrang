package com.ladysparks.ttaenggrang.domain.stock.dto;

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
public class StockTransactionResponseDTO {
    
    private Long studentId;                  // 학생 ID
    private Long stockId;                    // 거래된 주식 ID
    private String name;                     // 주식명
    private String type;                     // 주식 타입
    private TransactionType transactionType; // 거래 타입(BUY/SELL)
    private int shareCount;                  // 주식 거래 수량
    private Timestamp transactionDate;       // 거래 날짜
    private int purchasePricePerShare;       // 거래 당시 1주 가격
    
}