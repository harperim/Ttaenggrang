package com.ladysparks.ttaenggrang.domain.news.controller;

import com.ladysparks.ttaenggrang.domain.news.dto.NewsDTO;
import com.ladysparks.ttaenggrang.domain.news.dto.NewsSummaryDTO;
import com.ladysparks.ttaenggrang.domain.news.service.NewsService;
import com.ladysparks.ttaenggrang.global.docs.stock.NewsApiSpecification;
import com.ladysparks.ttaenggrang.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/news")
public class NewsController implements NewsApiSpecification {

    private final NewsService newsService;

    // 뉴스 [생성] Chatgpt 기반 : DB에 저장되지 않음, 미리보기 용도
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<NewsDTO>> createNews() {
        NewsDTO generatedNews = newsService.generateRandomNewsFromStocks();
        return ResponseEntity.ok(ApiResponse.success(generatedNews));
    }

    // 뉴스 [확인 후 저장]
    @PostMapping("/confirm")
    public ResponseEntity<ApiResponse<NewsDTO>> confirmNews(@RequestBody NewsDTO newsDTO) {
        NewsDTO savedNews = newsService.confirmNews(newsDTO);
        return ResponseEntity.ok(ApiResponse.success(savedNews));
    }

    // 뉴스 목록 [전체 조회]
    @GetMapping("/list")
    public ResponseEntity<ApiResponse<List<NewsSummaryDTO>>> getNewslist() {
        List<NewsSummaryDTO> newsList = newsService.getClassNewsList();
        return ResponseEntity.ok(ApiResponse.success(newsList));
    }

    // 뉴스 목록 [상세 조회]
    @GetMapping("/{newsId}")
    public ResponseEntity<ApiResponse<NewsSummaryDTO>> getNewsDetail(@PathVariable Long newsId) {
        NewsSummaryDTO newsDetail = newsService.getNewsDetail(newsId);
        return ResponseEntity.ok(ApiResponse.success(newsDetail));
    }

}
