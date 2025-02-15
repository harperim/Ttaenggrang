package com.ladysparks.ttaenggrang.domain.news.service;

import com.ladysparks.ttaenggrang.domain.news.dto.NewsDTO;
import com.ladysparks.ttaenggrang.domain.news.dto.NewsSummaryDTO;
import com.ladysparks.ttaenggrang.domain.news.entity.News;
import com.ladysparks.ttaenggrang.domain.news.entity.NewsType;
import com.ladysparks.ttaenggrang.domain.news.repository.NewsRepository;
import com.ladysparks.ttaenggrang.domain.stock.entity.Stock;
import com.ladysparks.ttaenggrang.domain.stock.repository.StockRepository;
import com.ladysparks.ttaenggrang.domain.teacher.entity.Teacher;
import com.ladysparks.ttaenggrang.domain.teacher.repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.stream.Collector;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class NewsService {

    @Value("${api.openai_key}")
    private String apiKey;

    private static final String API_URL = "https://api.openai.com/v1/chat/completions";
    private final NewsRepository newsRepository;
    private final StockRepository stockRepository;
    private final TeacherRepository teacherRepository;

    // 현재 로그인한 교사의 ID 가져오기
    private Long getTeacherIdFromSecurityContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalArgumentException("인증되지 않은 사용자입니다. 로그인 후 다시 시도하세요.");
        }

        Object principalObj = authentication.getPrincipal();
        if (principalObj instanceof UserDetails) {
            String email = ((UserDetails) principalObj).getUsername();  // 이메일 가져오기
            return teacherRepository.findByEmail(email)
                    .orElseThrow(() -> new IllegalArgumentException("해당 이메일을 가진 교사를 찾을 수 없습니다."))
                    .getId();
        }

        throw new IllegalArgumentException("현재 인증된 사용자를 찾을 수 없습니다.");
    }

    // 뉴스 기사 [생성] (확인 버튼을 누를 때까지 DB에 저장되지 않음)
    public NewsDTO generateRandomNewsFromStocks() {
        // 1. 모든 주식 엔터티에서 랜덤 선택
        List<Stock> stocks = stockRepository.findAll();
        if (stocks.isEmpty()) {
            throw new IllegalArgumentException("등록된 주식이 없습니다.");
        }
        Stock randomStock = stocks.get(new Random().nextInt(stocks.size()));

        // 2. chatGPT API 요청 준비
        String prompt = String.format(
                "초등학생이 이해할 수 있는 주식 시장 뉴스를 작성해주세요.\n" +
                        "주식명: %s\n" +
                        "조건: 내용은 반드시 **5문장 이상**으로 작성해주세요. 미만 시 재생성.\n" +
                        "형식:\n" +
                        "제목: [뉴스 제목]\n" +
                        "내용: [뉴스 내용 (최소 5문장)]\n" +
                        "유형: [호재/악재]\n" +
                        "반드시 위 형식을 정확히 따라야 합니다. **5문장 이상** 작성 필수!",
                randomStock.getName()
        );

        // GPT API 요청
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiKey);
        headers.set("Content-Type", "application/json");

        JSONObject requestBody = new JSONObject();
        JSONArray messages = new JSONArray();
        messages.put(new JSONObject().put("role", "user").put("content", prompt));
        requestBody.put("model", "gpt-3.5-turbo");
        requestBody.put("messages", messages);
        requestBody.put("max_tokens", 800);

        HttpEntity<String> entity = new HttpEntity<>(requestBody.toString(), headers);

        RestTemplate resttemplate = new RestTemplate();
        ResponseEntity<String> response = resttemplate.exchange(API_URL, HttpMethod.POST, entity, String.class);

        // 3️. 응답 파싱
        String responseBody = response.getBody();
        JSONObject jsonResponse = new JSONObject(responseBody);
        String generatedText = jsonResponse.getJSONArray("choices")
                .getJSONObject(0)
                .getJSONObject("message")
                .getString("content");

        // 4️. 뉴스 데이터 추출
        String title = extractValue(generatedText, "제목");
        String content = extractValue(generatedText, "내용");
        String newsTypeStr = extractValue(generatedText, "유형", "[호재/악재]");
        NewsType newsType = newsTypeStr.contains("호재") ? NewsType.POSITIVE : NewsType.NEGATIVE;

        // 6. DTO 반환 (DB에 저장 X)
        return NewsDTO.builder()
                .title(title)
                .content(content)
                .stockName(randomStock.getName())
                .createdAt(new Timestamp(System.currentTimeMillis()))
                .newsType(newsType.name())
                .build();
    }

    // 사용자가 확인 버튼을 누르면 DB에 저장
    public NewsDTO confirmNews(NewsDTO newsDTO) {
        Long teacherId = getTeacherIdFromSecurityContext();
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new IllegalArgumentException("해당 교사를 찾을 수 없습니다."));

        Stock stock = stockRepository.findByName(newsDTO.getStockName())
                .orElseThrow(() -> new IllegalArgumentException("해당 주식을 찾을 수 없습니다."));

        News news = News.builder()
                .title(newsDTO.getTitle())
                .content(newsDTO.getContent())
                .stock(stock)
                .createdAt(new Timestamp(System.currentTimeMillis()))
                .newsType(NewsType.valueOf(newsDTO.getNewsType()))
                .teacher(teacher)
                .build();

        newsRepository.save(news);
        return newsDTO;
    }

    // chatGPT 응답에서 다양한 키워드로 값 추출
    private String extractValue(String text, String... keys) {
        String[] lines = text.split("\n");
        for (String line : lines) {
            for (String key : keys) {
                if (line.contains(key + ":")) {
                    return line.replace(key + ":", "").trim();
                }
            }
        }
        return "알 수 없음";
    }

    // 문장 개수 확인 (마침표 기준)
    private int countSentences(String text) {
        if (text == null || text.isEmpty()) return 0;
        return text.split("[.!?]").length;
    }

    // 뉴스 기사 [전체 조회]
    public List<NewsSummaryDTO> getClassNewsList() {
        Long teacherId = getTeacherIdFromSecurityContext();

        List<News> newsList = newsRepository.findByTeacherId(teacherId);

        return newsList.stream()
                .map(news -> NewsSummaryDTO.builder()
                        .id(news.getId())
                        .title(news.getTitle())
                        .stockName(news.getStock().getName())
                        .createdAt(news.getCreatedAt())
                        .newsType(news.getNewsType().name())
                        .build())
                .collect(Collectors.toList());
    }
}
