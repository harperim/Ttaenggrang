package com.ladysparks.ttaenggrang.domain.stock.entity;

import com.ladysparks.ttaenggrang.domain.etf.entity.Etf;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor //모든 필드를 매개변수로 받는 생성자를 자동으로 생성
@NoArgsConstructor //기본 생성자(매개변수가 없는 생성자)를 자동으로 생성 , Entity 사용 하면 사용 해줘야함!
@Entity  // DB 매핑
@Builder  //Builder 패턴을 생성하여 객체를 생성
@Data
@Table(name = "marketStatus") //엔티티 클래스가 매핑될 DB 테이블의 이름을 지정
public class MarketStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private boolean isMarketActive;  // 현재 시장 활성화 상태
    private boolean isManualOverride;  // 선생님 버튼으로 수동 설정 여부


    //조인
    @ManyToOne
    @JoinColumn(name = "stock_id") // Stock과 연결
    private Stock stock;

    @ManyToOne
    @JoinColumn(name = "etf_id") // ETF와 연결
    private Etf etf;

}
