package com.ladysparks.ttaenggrang.domain.tax.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaxInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name; //세금명

    @Column(nullable = false)
    private int taxAmt; //사용 금액

    @Column(nullable = false)
    private String description; //설명

    @Column(nullable = false)
    private Timestamp useDate; //사용날짜

}
