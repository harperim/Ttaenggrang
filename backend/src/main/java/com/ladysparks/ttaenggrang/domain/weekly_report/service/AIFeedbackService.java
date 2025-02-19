package com.ladysparks.ttaenggrang.domain.weekly_report.service;

import com.ladysparks.ttaenggrang.domain.student.service.StudentService;
import com.ladysparks.ttaenggrang.domain.weekly_report.dto.WeeklyFinancialSummaryDTO;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class AIFeedbackService {

    private static final String API_URL = "https://api.openai.com/v1/chat/completions";
    private final StudentService studentService;

    public String generateWeeklyFeedback(WeeklyFinancialSummaryDTO summaryDTO, String type) {
        String studentName = studentService.findNameById(summaryDTO.getStudentId());

        String apiKey = System.getenv("OPENAI_API_KEY");

        String maskedKey = (apiKey != null && apiKey.length() >= 5)
                ? apiKey.substring(0, 5) + "*****"
                : "Invalid Key";

        System.out.println("debug: " + maskedKey);

        // chatGPT API 요청 준비
        String prompt = String.format(
                "1. 주어진 데이터\n" +
                "- 학생 이름: %s\n" +
                "- totalIncome: %d\n" +
                "- salaryAmount: %d\n" +
                "- savingsAmount: %d\n" +
                "- investmentReturn: %d\n" +
                "- incentiveAmount: %d\n" +
                "- totalExpenses: %d\n" +
                "- taxAmount: %d\n" +
                "- fineAmount: %d\n" +
                "- 이번주 유형: %s (투자형/소비형/저축형 중 하나)\n" +
                "\n" +
                "2. 조건\n" +
                "- 이번주 유형 값과 주어진 데이터에 기반하여 가이드를 2~3개 제시\n" +
                "- 투자형/소비형/저축형 중 어떤 유형인지 언급하고 이에 맞게 피드백 생성\n" +
                "- '당신'이라고 하지 말고 학생 이름으로 지칭\n" +
                "- 초등학생이 이해할 수 있도록 쉬운 말로 설명\n" +
                "- 예를 들어, 저축을 많이 했으면 칭찬하고 소비가 많다면 절약을 추천하는 식으로 작성\n" +
                "- **반드시 아래 형식으로 출력할 것!**\n" +
                "report: [피드백 내용]\n",
                studentName,
                summaryDTO.getTotalIncome(),
                summaryDTO.getSalaryAmount(),
                summaryDTO.getSavingsAmount(),
                summaryDTO.getInvestmentReturn(),
                summaryDTO.getIncentiveAmount(),
                summaryDTO.getTotalExpenses(),
                summaryDTO.getTaxAmount(),
                summaryDTO.getFineAmount(),
                type
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
        requestBody.put("max_tokens", 1000);

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
        return extractReport(generatedText);
    }

    // chatGPT 응답에서 다양한 키워드로 값 추출
    private String extractReport(String text) {
        if (text == null || text.trim().isEmpty()) {
            return "❗ GPT 응답이 비어 있습니다.";
        }

        // "report:" 이후의 모든 내용 추출
        String keyword = "report:";
        int startIndex = text.toLowerCase().indexOf(keyword.toLowerCase());

        if (startIndex == -1) {
            return "⚠️ 'report:' 항목을 찾을 수 없습니다. 원본 응답: " + text;
        }

        // "report:" 이후의 텍스트 추출
        String reportContent = text.substring(startIndex + keyword.length()).trim();

        if (reportContent.isEmpty()) {
            return "⚠️ 'report:' 내용이 없습니다.";
        }

        // "1.", "2.", "3." 등 숫자 제거
        reportContent = reportContent.replaceAll("(?m)^\\d+\\.\\s*", "");

        // 큰따옴표(`"`) 제거
        reportContent = reportContent.replaceAll("\"", "");

        // "\n" 제거
        reportContent = reportContent.replaceAll("\\s*\\n+\\s*", " ");

        return reportContent;
    }

}
