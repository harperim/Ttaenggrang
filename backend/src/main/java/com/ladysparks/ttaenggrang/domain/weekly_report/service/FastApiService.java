package com.ladysparks.ttaenggrang.domain.weekly_report.service;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.http.MediaType;
import reactor.core.publisher.Mono;
import java.util.Map;

@Service
public class FastApiService {

    private final WebClient webClient;

    public FastApiService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
                .baseUrl("https://i12d107.p.ssafy.io/api/fastapi")  // ✅ Fly.io 대신 Nginx를 경유
                .defaultHeader("Content-Type", "application/json")
                .defaultHeader("User-Agent", "Spring Boot WebClient") // ✅ User-Agent 추가
                .build();
    }

    public Mono<String> predictCluster(int totalIncome, int totalExpense, int total_savings, int totalInvestment) {
        return webClient.post()
                .uri("/predict-cluster")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of(
                        "student_id", 0,
                        "total_income", totalIncome,
                        "total_expense", totalExpense,
                        "total_savings", total_savings,
                        "total_investment", totalInvestment
                ))
                .retrieve()
                .bodyToMono(String.class)  // ✅ 제네릭 명확히 지정
                .doOnError(error -> System.out.println("🔴 FastAPI 호출 실패: " + error.getMessage()));
    }

}
