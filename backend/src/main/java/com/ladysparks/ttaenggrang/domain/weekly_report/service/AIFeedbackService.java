package com.ladysparks.ttaenggrang.domain.weekly_report.service;

import com.ladysparks.ttaenggrang.domain.weekly_report.dto.WeeklyFinancialSummaryDTO;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class AIFeedbackService {

    private static final String API_URL = "https://api.openai.com/v1/chat/completions";

    public String generateWeeklyFeedback(WeeklyFinancialSummaryDTO summaryDTO, String cluster) {
        String apiKey = System.getenv("OPENAI_API_KEY");

        String maskedKey = (apiKey != null && apiKey.length() >= 5)
                ? apiKey.substring(0, 5) + "*****"
                : "Invalid Key";

        System.out.println("debug: " + maskedKey);

        // chatGPT API 요청 준비
        String prompt = String.format(
                "1. 피드백 대상 : 초등학생\n" +
                "2. 출력 조건 : 내용은 반드시 **2문장 이상**으로 작성해주세요. 미만 시 재생성\n" +
                "3. 요구사항 : 이번 주에 학생이 진행한 경제 홛동 데이터를 통해 학생에게 필요한 피드백을 생성해주세요. 문어체 사용.\n" +
                "4. 학생의 총 수입: %s\n" +
                "5. 학생의 총 지출: %s\n" +
                "6. 학생의 총 투자 수익: %s\n" +
                "7. 학생의 경제 활동 유형(군집화 모델을 통해 분류됨): %s\n" +
                "8. 형식:\n" +
                "피드백: [피드백 내용]\n" +
                "반드시 위 형식을 정확히 따라야 합니다. **2문장 이상** 작성 필수!",                summaryDTO.getTotalIncome(),
                summaryDTO.getInvestmentReturn(),
                summaryDTO.getTotalExpenses(),
                cluster
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

        // 응답 파싱
        String responseBody = response.getBody();
        JSONObject jsonResponse = new JSONObject(responseBody);
        String generatedText = jsonResponse.getJSONArray("choices")
                .getJSONObject(0)
                .getJSONObject("message")
                .getString("content");

        // 피드백 데이터 추출
        return extractValue(generatedText, "피드백");
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

}
