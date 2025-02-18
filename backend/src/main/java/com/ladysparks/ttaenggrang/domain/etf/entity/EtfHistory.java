package com.ladysparks.ttaenggrang.domain.etf.entity;

import com.ladysparks.ttaenggrang.domain.stock.entity.Stock;
import com.ladysparks.ttaenggrang.domain.stock.entity.StockTransaction;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@AllArgsConstructor // 모든 필드를 매개변수로 받는 생성자를 자동으로 생성
@NoArgsConstructor // 기본 생성자(매개변수가 없는 생성자)를 자동으로 생성 , Entity 사용 하면 사용 해줘야함!
@Builder  //Builder 패턴을 생성하여 객체를 생성
@Data
@Entity
@Table(name = "etf_history")
public class EtfHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private int price;              // 해당 날짜의 주식 가격

    @Column
    private int buyVolume;          // 해당 날짜의 매수량

    @Column
    private int sellVolume;         // 해당 날짜의 매도량

    @Column
    private int priceChangeRate;    // 가격 변동률

    @Column
    private Timestamp createdAt;    // 생성일

    // 조인
    // Etf
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "etf_id")
    private Etf Etf;

    // 주식 거래 내역 (학생의 매수/매도 기록)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "etf_transaction_id")
    private EtfTransaction etfTransaction; // 주식 거래 내역

}