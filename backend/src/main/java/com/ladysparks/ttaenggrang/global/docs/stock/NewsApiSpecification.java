package com.ladysparks.ttaenggrang.global.docs.stock;

import com.ladysparks.ttaenggrang.domain.news.dto.NewsDTO;
import com.ladysparks.ttaenggrang.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;


@Tag(name = "[교사] 뉴스", description = "NEWS API")
public interface NewsApiSpecification {


//    public ResponseEntity<ApiResponse<NewsDTO>> createNews();
    @Operation(summary = "(교사) 뉴스 생성", description = "💡 뉴스를 생성 합니다")
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<NewsDTO>> generateNews();

//    @Operation(summary = "뉴스 생성 조회", description = "💡 뉴스 생성 조회")
//    @GetMapping("/generate-news")
//    public ResponseEntity<ApiResponse<NewsDTO>> generateNews();
}
