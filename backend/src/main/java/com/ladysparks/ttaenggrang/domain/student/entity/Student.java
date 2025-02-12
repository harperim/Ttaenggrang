package com.ladysparks.ttaenggrang.domain.student.entity;

import com.ladysparks.ttaenggrang.domain.bank.entity.BankAccount;
import com.ladysparks.ttaenggrang.domain.teacher.entity.Job;
import com.ladysparks.ttaenggrang.domain.teacher.entity.Nation;
import com.ladysparks.ttaenggrang.domain.teacher.entity.Teacher;
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
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;  // 학생 계정 (베이스ID + 숫자)

    @Column(nullable = false)
    private String password;  // 초기 비밀번호 (베이스ID와 동일)

    @Column(length = 50)
    private String name;  // 학생 이름

    @Column(length = 2083)
    private String profileImageUrl;  // ✅ AWS S3 이미지 URL 저장 필드

    @Column(nullable = false, updatable = false)
    private Timestamp createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = new Timestamp(System.currentTimeMillis());
    }

    // <조인>
    @ManyToOne
    @JoinColumn(name = "teacher_id", nullable = false)
    private Teacher teacher;

    @OneToOne
    @JoinColumn(name = "bank_account_id")  // nullable = false
    private BankAccount bankAccount;

    @ManyToOne
    @JoinColumn(name = "job_id")
    private Job job;

    public Student(Long id) {
        this.id = id;
    }

    //주식 거래내역
//    @OneToMany(mappedBy = "student", fetch = FetchType.LAZY)
//    private List<StockTransaction> transactions;

    //ETF 거래내역
//    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL)
//    private List<EtfTransaction> etfTransactions;
}
