package com.ladysparks.ttaenggrang.domain.tax.entity;

import com.ladysparks.ttaenggrang.domain.teacher.entity.Nation;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.CreatedDate;

import java.sql.Timestamp;
import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaxUsage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "nation_id", nullable = false)
    private Nation nation;

    @Column(nullable = false)
    private String name; // 세금명

    @Column(nullable = false)
    private int amount; // 사용 금액

    @Column(nullable = false)
    private String description; // 설명

    @CreationTimestamp
    private LocalDate usageDate; // 사용 날짜

}
