package com.ladysparks.ttaenggrang.domain.stock.entity;

import com.ladysparks.ttaenggrang.domain.news.entity.News;
import com.ladysparks.ttaenggrang.domain.stock.category.Category;
import com.ladysparks.ttaenggrang.domain.etf.entity.Etf;
import com.ladysparks.ttaenggrang.domain.teacher.entity.Teacher;
import jakarta.persistence.*;
import lombok.*;


import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
@AllArgsConstructor //모든 필드를 매개변수로 받는 생성자를 자동으로 생성
@NoArgsConstructor //기본 생성자(매개변수가 없는 생성자)를 자동으로 생성 , Entity 사용 하면 사용 해줘야함!
@Entity  // DB 매핑
@Builder  //Builder 패턴을 생성하여 객체를 생성
@Data
@Table(name = "stock")  //엔티티 클래스가 매핑될 DB 테이블의 이름을 지정
public class Stock {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;            // 주식 ID

    @Column
    private String name;        // 주식 이름

    @Column
    private int price_per;// 한 주당 가격(기본 가격)

    @Column
    private int total_qty;      // 회사가 보유 한 주식 총 수량

    @Column
    private int remain_qty;     // 주식 재고 수량

    @Column
    private String description; // 설명

    @Column
    private Timestamp created_at; // 생성일

    @Column
    private Timestamp updated_at;  // 수정일

    @Column
    private String categoryName;  //카테고리 이름


    @Column(nullable = false, updatable = false)  // updatable = false -> 수정 불가
    private final String type = "일반 주식";


    @Column
    private Integer changeRate; //가격 변동률


    @Column
    private LocalDateTime priceChangeTime;  // 가격 변동 시간

    @Column
    private BigDecimal weight;  // 주식 비중 (각 주식의 비중을 따로 저장)


    //조인

    //교사
    @ManyToOne(targetEntity = Teacher.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id") // 외래 키(Foreign Key) 설정
    private Teacher teacher;

    //과거 추이 기록
    @OneToMany(mappedBy = "stock", fetch = FetchType.LAZY)
    private List<StockHistory> stockHistories; // 여러 과거 추이 기록


    // 주식 거래 내역
    @OneToMany(mappedBy = "stock", fetch = FetchType.LAZY)
    private List<StockTransaction> stockTransaction;

    // ETF와 다대일 관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "etf_id")
    private Etf etf;

    @ManyToOne
    @JoinColumn(name = "category_id") // 외래 키 컬럼 설정
    private Category category; // 카테고리

    //뉴스
    @OneToMany(targetEntity = Stock.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "news_id")
    private List<News> news;

    @ManyToOne
    @JoinColumn(name = "market_status_id")
    private StockMarketStatus stockMarketStatus; // MarketStatus와 연결



}