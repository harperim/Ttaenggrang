package com.ladysparks.ttaenggrang.domain.news.dto;

import com.ladysparks.ttaenggrang.domain.news.entity.NewsType;
import lombok.*;

import java.sql.Timestamp;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NewsDTO {
    private Long id;            // 뉴스 ID
    private String title;       // 뉴스 제목
    private String content;     // 뉴스 내용
    private float impact;       // 영향 비율
    private String created_by;  // 생성자
    private Timestamp created_at; // 생성일
    private NewsType newsType;   //뉴스 타입(호재,악재)


    // 주식 관련 리스트 (주식과의 관계를 나타내는 리스트)
    private List<Long> stockIds; // 뉴스에 연결된 주식들의 ID 리스트

    // ETF 관련 리스트 (ETF와의 관계를 나타내는 리스트)
    private List<Long> etfIds;   // 뉴스에 연결된 ETF들의 ID 리스트


}
