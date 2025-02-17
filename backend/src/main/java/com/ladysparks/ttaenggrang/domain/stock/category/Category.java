package com.ladysparks.ttaenggrang.domain.stock.category;

import com.ladysparks.ttaenggrang.domain.stock.entity.Stock;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "category")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // 카테고리 ID

    @Column(nullable = false)
    private String name;  // 카테고리 이름

    @Column
    private String description;  // 카테고리 설명

    @OneToMany(mappedBy = "category")
    private List<Stock> stocks;
}