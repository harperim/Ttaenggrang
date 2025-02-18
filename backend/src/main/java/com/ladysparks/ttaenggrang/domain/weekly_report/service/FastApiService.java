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
                .baseUrl("https://ttaenggrang.fly.dev") // ✅ FastAPI 엔드포인트
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    public Mono<Map<String, Object>> predictCluster(int totalIncome, int totalExpense, int totalInvestment,
                                                    int investmentReturn, int taxPaid, int finePaid, int incentive) {
        return webClient.post()
                .uri("/predict-cluster")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of(
                        "total_income", totalIncome,
                        "total_expense", totalExpense,
                        "total_investment", totalInvestment,
                        "investment_return", investmentReturn,
                        "tax_paid", taxPaid,
                        "fine_paid", finePaid,
                        "incentive", incentive
                ))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})  // ✅ 제네릭 명확히 지정
                .doOnError(error -> System.out.println("🔴 FastAPI 호출 실패: " + error.getMessage()));
    }

}
