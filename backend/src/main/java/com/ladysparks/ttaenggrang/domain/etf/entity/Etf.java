package com.ladysparks.ttaenggrang.domain.etf.entity;

import com.ladysparks.ttaenggrang.domain.news.entity.News;
import com.ladysparks.ttaenggrang.domain.stock.category.Category;
import com.ladysparks.ttaenggrang.domain.stock.entity.StockMarketStatus;
import com.ladysparks.ttaenggrang.domain.stock.entity.Stock;
import com.ladysparks.ttaenggrang.domain.stock.entity.StockHistory;
import com.ladysparks.ttaenggrang.domain.teacher.entity.Teacher;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
@AllArgsConstructor //모든 필드를 매개변수로 받는 생성자를 자동으로 생성
@NoArgsConstructor //기본 생성자(매개변수가 없는 생성자)를 자동으로 생성 , Entity 사용 하면 사용 해줘야함!
@Entity
@Data
@Builder
@Table(name = "etf")
public class Etf {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name; //ETF 이름

    @Column
    private int price_per; // 한 주당 가격

    @Column
    private int total_qty;      // 총 수량

    @Column
    private int remain_qty;     // ETF 재고 수량

    @Column
    private String description; // 설명

    @Column
    private Timestamp created_at; // 생성일

    @Column
    private Timestamp updated_at;  // 수정일

    @Column(precision = 5, scale = 4)  //비중
    private BigDecimal weight;

    @Column
    private Integer changeRate; //가격 변동률


    @Column(nullable = false, updatable = false)
    private final String type = "ETF";

    @Column(columnDefinition = "TEXT")
    private String stockDataJson; // 주식 ID + 수량을 JSON 형태로 저장


    // 조인

    //선생님
    @ManyToOne(targetEntity = Teacher.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id") // 외래 키(Foreign Key) 설정
    private Teacher teacher;

    //과거 추이 기록
    @OneToMany(mappedBy = "etf", fetch = FetchType.LAZY)
    private List<StockHistory> stockHistories; // 여러 과거 추이 기록

    //주식
    @OneToMany(targetEntity = Stock.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "etf_id")
    private List<Stock> stocks;

    //ETF 거래 내역
    @OneToMany(targetEntity = EtfTransaction.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "etfTransaction_id")
    private List<EtfTransaction> etfTransaction;

    //뉴스
    @OneToMany(targetEntity = Stock.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "newa_id")
    private List<News> news;


    @ManyToOne(targetEntity = Category.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne
    @JoinColumn(name = "market_status_id")
    private StockMarketStatus stockMarketStatus; // MarketStatus와 연


}
