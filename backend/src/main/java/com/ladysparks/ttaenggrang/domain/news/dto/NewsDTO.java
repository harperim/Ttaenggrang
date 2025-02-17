package com.ladysparks.ttaenggrang.domain.news.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@Builder
public class NewsDTO {

    private String title;
    private String content;
    private String stockName;
    private Timestamp createdAt;
    private String newsType;  // 악재, 호재 (Negative, Positive)
}
