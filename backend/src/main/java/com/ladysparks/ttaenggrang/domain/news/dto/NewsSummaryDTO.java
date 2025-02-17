package com.ladysparks.ttaenggrang.domain.news.dto;

import lombok.Builder;
import lombok.Getter;

import java.sql.Timestamp;

@Getter
@Builder
public class NewsSummaryDTO {

    private Long id;
    private String title;
    private String content;
    private String stockName;
    private Timestamp createdAt;
    private String newsType;
}
