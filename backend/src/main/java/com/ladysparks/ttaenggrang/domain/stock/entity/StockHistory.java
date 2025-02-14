package com.ladysparks.ttaenggrang.domain.stock.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.ladysparks.ttaenggrang.domain.etf.entity.Etf;
import com.ladysparks.ttaenggrang.domain.etf.entity.EtfTransaction;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@AllArgsConstructor //모든 필드를 매개변수로 받는 생성자를 자동으로 생성
@NoArgsConstructor //기본 생성자(매개변수가 없는 생성자)를 자동으로 생성 , Entity 사용 하면 사용 해줘야함!
@Entity  // DB 매핑
@Builder  //Builder 패턴을 생성하여 객체를 생성
@Data
public class StockHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private int price;  //해당 날짜의 주식 가격

    @Column
    private int buyVolume; //해당 날짜의 거래량

    @Column
    private int sellVolume;

    @Column
    private Timestamp date;  //기록된 날짜


    //조인

    //주식
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_id")
    private Stock stock;


    //ETF
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "etf_id")
    private Etf etf;  // ETF

    // ETF 거래 기록 (매수/매도 내역)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "etf_transaction_id")
    private EtfTransaction etfTransaction;

    // 주식 거래 내역 (학생의 매수/매도 기록)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_transaction_id")
    private StockTransaction stockTransaction;  // 주식 거래 내역

}
