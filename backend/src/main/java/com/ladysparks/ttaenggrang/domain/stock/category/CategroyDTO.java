package com.ladysparks.ttaenggrang.domain.stock.category;

import com.ladysparks.ttaenggrang.domain.stock.entity.Stock;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategroyDTO {
    private Long id;
    private String name;  // 카테고리 이름
    private String description;  // 카테고리 설명
    private List<Long> stockIds;  // 카테고리와 연결된 주식 목록

}
