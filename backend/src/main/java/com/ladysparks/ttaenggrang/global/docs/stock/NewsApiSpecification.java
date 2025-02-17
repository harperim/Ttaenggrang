package com.ladysparks.ttaenggrang.global.docs.stock;

import com.ladysparks.ttaenggrang.domain.news.dto.NewsDTO;
import com.ladysparks.ttaenggrang.domain.news.dto.NewsSummaryDTO;
import com.ladysparks.ttaenggrang.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.json.JSONPropertyIgnore;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.IOException;
import java.util.List;

@Tag(name = "[교사/학생] 뉴스 관리", description = "뉴스 관련 API")
public interface NewsApiSpecification {

    @Operation(summary = "(교사) 뉴스 생성", description = """
            💡 버튼 클릭 시 ChatGPT API를 통해 랜덤 뉴스 생성  
            
            - 생성된 뉴스는 `DB에 저장되지 않음`  
            - 사용자가 내용을 확인한 후 저장 여부를 결정해야 함  
            - 응답 데이터에 뉴스 제목, 내용, 주식명, 뉴스 유형(호재/악재) 포함  
            """)
    @PostMapping("/news/create")
    ResponseEntity<ApiResponse<NewsDTO>> createNews();

    @Operation(summary = "(교사) 뉴스 저장", description = """
            💡 뉴스 생성 후 **확인 버튼 클릭 시 저장**  

            - 생성된 뉴스를 `DB에 저장`  
            - 저장된 뉴스는 **우리 반 학생들도 조회 가능**  
            - `teacherId`를 기준으로 저장됨  
            """)
    @PostMapping("/news/confirm")
    ResponseEntity<ApiResponse<NewsDTO>> confirmNews(@RequestBody NewsDTO newsDTO) throws IOException;

    @Operation(summary = "(교사/학생) 뉴스 전체 목록 조회", description = """
            💡 **현재 로그인한 교사가 만든 뉴스 목록을 조회**
            
            - **교사와 학생 모두** 조회 가능
            - `우리 반 학생들`만 해당 반의 뉴스를 조회할 수 있음
            - 최신 뉴스부터 정렬됨 (`createdAt DESC`)
            """)
    @GetMapping("/news/list")
    ResponseEntity<ApiResponse<List<NewsSummaryDTO>>> getNewslist();

    @Operation(summary = "(교사/학생) 뉴스 상세 목록 조회", description = """
            💡 **특정 뉴스의 상세 정보 조회**

            - **교사와 학생 모두** 조회 가능
            - `newsId`를 기준으로 특정 뉴스 조회
            - 응답 데이터에 `뉴스 작성자(교사) 정보 포함`
            """)
    @GetMapping("/news/{newsId}")
    ResponseEntity<ApiResponse<NewsSummaryDTO>> getNewsDetail(@PathVariable Long newsId);
}
