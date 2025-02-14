package com.ladysparks.ttaenggrang.global.docs.stock;

import com.ladysparks.ttaenggrang.domain.news.dto.NewsDTO;
import com.ladysparks.ttaenggrang.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;

@Tag(name = "[교사] 뉴스 생성", description = "뉴스 생성 관련 API")
public interface NewsApiSpecification {

    @Operation(summary = "뉴스 생성", description = "💡 버튼 클릭 시 ChatGPT API를 통해 랜덤 뉴스 생성")
    @PostMapping("/news/create")
    ResponseEntity<ApiResponse<NewsDTO>> createNews();
}
