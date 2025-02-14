package com.ladysparks.ttaenggrang.domain.stock.dto;

import com.ladysparks.ttaenggrang.domain.stock.entity.TransType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.sql.Timestamp;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionResponseDTO {
    private Long studentId;       // 학생 ID
    private Long stockId;         // 거래된 주식 ID
    private String Name;     // 주식명
    private String Type;     // 주식 타입
    private TransType transType;
    private int share_count;  // 주식 거래 수량
    private Timestamp trans_date;  // 거래 날짜
    private int purchase_prc;   // 거래 당시 1주 가격
}
