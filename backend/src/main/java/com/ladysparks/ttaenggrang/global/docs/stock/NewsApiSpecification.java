package com.ladysparks.ttaenggrang.global.docs.stock;

import com.ladysparks.ttaenggrang.domain.news.dto.NewsDTO;
import com.ladysparks.ttaenggrang.domain.news.dto.NewsSummaryDTO;
import com.ladysparks.ttaenggrang.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.json.JSONPropertyIgnore;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Tag(name = "[교사/학생] 뉴스 관리", description = "뉴스 관련 API")
public interface NewsApiSpecification {

    @Operation(summary = "(교사) 뉴스 생성", description = "💡 버튼 클릭 시 ChatGPT API를 통해 랜덤 뉴스 생성")
    @PostMapping("/news/create")
    ResponseEntity<ApiResponse<NewsDTO>> createNews();

    @Operation(summary = "(교사) 뉴스 저장", description = "💡 뉴스 생성 후 확인 버튼 클릭 시, 저장")
    @PostMapping("/news/confirm")
    ResponseEntity<ApiResponse<NewsDTO>> confirmNews(@RequestBody NewsDTO newsDTO);

    @Operation(summary = "(교사/학생) 뉴스 전체 목록 조회", description = "💡 현재 로그인한 교사가 만든 뉴스 목록을 가져와 교사/학생이 전체 목록을 조회합니다.")
    @GetMapping("/news/list")
    ResponseEntity<ApiResponse<List<NewsSummaryDTO>>> getNewslist();
}
