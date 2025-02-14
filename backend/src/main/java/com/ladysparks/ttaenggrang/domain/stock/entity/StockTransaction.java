package com.ladysparks.ttaenggrang.domain.stock.entity;

import com.ladysparks.ttaenggrang.domain.student.entity.Student;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;

@AllArgsConstructor //모든 필드를 매개변수로 받는 생성자를 자동으로 생성
@NoArgsConstructor //기본 생성자(매개변수가 없는 생성자)를 자동으로 생성 , Entity 사용 하면 사용 해줘야함!
@Entity  // DB 매핑
@Builder  //Builder 패턴을 생성하여 객체를 생성
@Data
@Table(name = "stock_transaction")  //엔티티 클래스가 매핑될 DB 테이블의 이름을 지정
public class StockTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private int share_count;  // 주식 거래 수량

    @Column
    private Timestamp trans_date;  // 거래 날짜

    @Column
    private int purchase_prc;   // 거래 당시 1주 가격

    @Column
    private int owned_qty;      // 학생이 보유한 주식 수량

    @Column
    private Integer  total_amt;  // 총 거래 금액


    @Column(precision = 5, scale = 2)  // DECIMAL(5, 2) 설정
    private BigDecimal returnRate;  // 손익/손실 금액


    // 거래 유형
    @Enumerated(EnumType.STRING)  // Enum 값을 문자열로 저장
    @Column(name = "trans_type")
    private TransactionType transactionType;


    //조인

    //학생
    @ManyToOne(targetEntity = Student.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = true)  // 학생 정보가 없을 수도 있음을 나타냄
    private Student student;



    //주식
    @ManyToOne(targetEntity = Stock.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_id")
    private Stock stock;



}

