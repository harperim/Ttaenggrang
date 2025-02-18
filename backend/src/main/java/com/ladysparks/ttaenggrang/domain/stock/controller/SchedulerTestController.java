package com.ladysparks.ttaenggrang.domain.stock.controller;

import com.ladysparks.ttaenggrang.domain.stock.scheduler.StockMarketScheduler;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Scheduler 테스트용")
@RestController
@RequestMapping("/admin/scheduler")
@RequiredArgsConstructor
public class SchedulerTestController {

    private final StockMarketScheduler stockMarketScheduler;

    @PostMapping("/open")
    public ResponseEntity<String> testMarketOpen() {
        stockMarketScheduler.autoMarketOpen();
        return ResponseEntity.ok("🚀 주식시장 개장 테스트 완료!");
    }

    @PostMapping("/close")
    public ResponseEntity<String> testMarketClose() {
        stockMarketScheduler.autoMarketClose();
        return ResponseEntity.ok("🚀 주식시장 폐장 테스트 완료!");
    }

//    @PostMapping("/weekly-report")
//    public ResponseEntity<String> testWeeklyReport() {
//        stockMarketScheduler.scheduleWeeklyReportGeneration();
//        return ResponseEntity.ok("📊 주간 금융 리포트 생성 테스트 완료!");
//    }

}

