package com.ladysparks.ttaenggrang.domain.news.entity;

import com.ladysparks.ttaenggrang.domain.etf.entity.Etf;
import com.ladysparks.ttaenggrang.domain.stock.entity.Stock;
import com.ladysparks.ttaenggrang.domain.stock.entity.TransType;
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
@Table(name = "news")  //엔티티 클래스가 매핑될 DB 테이블의 이름을 지정
public class News {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;            // 뉴스 ID
    @Column
    private String title;       // 뉴스 제목
    @Column
    private String content;     // 뉴스 내용
    @Column
    private float impact;       // 영향 비율
    @Column
    private String created_by;  // 생성자
    @Column
    private Timestamp created_at; // 생성일

    @Enumerated(EnumType.STRING)  // Enum 값을 문자열로 저장
    @Column(name = "news_type")
    private NewsType newsType;



    //조인
    @ManyToOne
    @JoinColumn(name = "stock_id")  //주식
    private Stock stock;

    @ManyToOne
    @JoinColumn(name = "etf_id")  //ETF
    private Etf etf;


}
