package com.ladysparks.ttaenggrang.domain.teacher.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.ladysparks.ttaenggrang.domain.etf.entity.Etf;
import com.ladysparks.ttaenggrang.domain.stock.entity.StockMarketStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.List;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "teacher")
public class Teacher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;            // 선생님 ID

    @Column(length = 50)
    private String name;

    @Column
    private String password;

    @Column(length = 100, unique = true, nullable = false)
    private String email;

    @Column(length = 50, nullable = false)
    private String school;

    @Column
    private String fcmToken;

    @Column(nullable = false, updatable = false)
    private Timestamp createdAt;

    // 주식
    @OneToMany(mappedBy = "teacher", fetch = FetchType.LAZY)
    private List<Etf> etfs; // 선생님이 관리하는 주식 목록

    @OneToOne(mappedBy = "teacher", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonIgnore
    private Nation nation;

    public Teacher(Long id) {
        this.id = id;
    }

    @OneToOne(mappedBy = "teacher", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties({"teacher"})
    private StockMarketStatus marketStatus;

}
