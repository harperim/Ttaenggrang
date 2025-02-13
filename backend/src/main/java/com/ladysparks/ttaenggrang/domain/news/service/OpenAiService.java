package com.ladysparks.ttaenggrang.domain.news.service;


import com.ladysparks.ttaenggrang.domain.news.dto.NewsDTO;
import com.ladysparks.ttaenggrang.domain.news.entity.NewsType;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;


//@Service
//public class OpenAiService {
//
//    @Value("${news.api-key}")
//    private String apiKey;  // application.yml에서 API 키 값을 주입
//
//    private static final String API_URL = "https://api.openai.com/v1/completions";
//
//    public NewsDTO generateNews(String prompt) {
//        // RestTemplate 초기화 (API 호출을 위한 HTTP 클라이언트)
//        RestTemplate restTemplate = new RestTemplate();
//
//        // Spring의 HttpHeaders를 사용
//        HttpHeaders headers = new HttpHeaders();
//        headers.set("Authorization", "Bearer " + apiKey);  // apiKey를 실제 값으로 설정
//        headers.set("Content-Type", "application/json");
//
//        // 요청 본문 설정 (전달받은 프롬프트 사용)
//        String body = "{ \"model\": \"text-davinci-003\", \"prompt\": \"" + prompt + "\", \"max_tokens\": 500 }";
//
//        // Spring의 HttpEntity 사용 (제네릭 타입 지정)
//        HttpEntity<String> entity = new HttpEntity<>(body, headers);
//
//        // API 호출
//        ResponseEntity<String> response = restTemplate.exchange(API_URL, HttpMethod.POST, entity, String.class);
//
//        // 응답 본문 파싱
//        String responseBody = response.getBody();
//
//        try {
//            JSONObject jsonResponse = new JSONObject(responseBody);
//            JSONArray choices = jsonResponse.getJSONArray("choices");
//            String text = choices.getJSONObject(0).getString("text").trim();
//
//            // 응답에서 뉴스 제목, 내용, 호재/악재를 분리
//            String title = extractTitle(text);
//            String content = extractContent(text);
//            String newsType = extractNewsType(text);
//
//            // DTO 객체로 반환
//            return NewsDTO.builder().title(title).content(content).newsType(NewsType.valueOf(newsType)).build();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw new IllegalArgumentException("뉴스 생성에 실패했습니다.");
//        }
//    }
//
//    // 뉴스 제목 추출
//    private String extractTitle(String text) {
//        String[] lines = text.split("\n");
//        return lines[0].replace("뉴스 제목: ", "").trim();
//    }
//
//    // 뉴스 내용 추출
//    private String extractContent(String text) {
//        String[] lines = text.split("\n");
//        StringBuilder content = new StringBuilder();
//        for (int i = 1; i < lines.length - 1; i++) {
//            content.append(lines[i]).append("\n");
//        }
//        return content.toString().trim();
//    }
//
//    // 뉴스 타입 (호재/악재) 추출
//    private String extractNewsType(String text) {
//        String[] lines = text.split("\n");
//        return lines[lines.length - 1].replace("호재/악재: ", "").trim();
//    }
//}

