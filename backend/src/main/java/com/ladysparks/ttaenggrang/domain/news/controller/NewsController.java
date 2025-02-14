package com.ladysparks.ttaenggrang.domain.news.controller;

import com.ladysparks.ttaenggrang.domain.news.dto.NewsDTO;
import com.ladysparks.ttaenggrang.domain.news.service.NewsService;
import com.ladysparks.ttaenggrang.global.docs.stock.NewsApiSpecification;
import com.ladysparks.ttaenggrang.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/news")
public class NewsController implements NewsApiSpecification {

    private final NewsService newsService;

    // 뉴스 [생성] Chatgpt 기반
    @PostMapping
    public ResponseEntity<ApiResponse<NewsDTO>> createNews() {
        NewsDTO generatedNews = newsService.generateRandomNewsFromStocks();
        return ResponseEntity.ok(ApiResponse.success(generatedNews));
    }

}
