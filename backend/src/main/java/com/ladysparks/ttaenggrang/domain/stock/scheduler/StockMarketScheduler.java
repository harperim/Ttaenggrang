package com.ladysparks.ttaenggrang.domain.stock.scheduler;

import com.ladysparks.ttaenggrang.domain.stock.service.StockMarketStatusService;
import com.ladysparks.ttaenggrang.global.redis.RedisLockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
// @Profile("prod")  // 운영 환경에서만 스케줄러 실행
public class StockMarketScheduler {

    private final StockMarketStatusService stockMarketService;
    private final RedisLockService redisLockService;

    private static final String MARKET_OPEN_LOCK = "stockMarket:open";
    private static final String MARKET_CLOSE_LOCK = "stockMarket:close";

    // ✅ 평일 09:00 자동 개장 (Redis Lock 적용)
    @Scheduled(cron = "0 0 9 * * MON-SAT", zone = "Asia/Seoul")
    public void autoMarketOpen() {
        if (redisLockService.acquireLock(MARKET_OPEN_LOCK)) {
            try {
                log.info("🚀 [운영] 09:00 주식시장 자동 개장 시작");
                stockMarketService.autoMarketOpen();
            } finally {
                redisLockService.releaseLock(MARKET_OPEN_LOCK);
            }
        }
    }

    // ✅ 평일 17:00 자동 폐장 (Redis Lock 적용)
    @Scheduled(cron = "0 0 17 * * MON-SAT", zone = "Asia/Seoul")
    public void autoMarketClose() {
        if (redisLockService.acquireLock(MARKET_CLOSE_LOCK)) {
            try {
                log.info("🚀 [운영] 17:00 주식시장 자동 폐장 시작");
                stockMarketService.autoMarketClose();
            } finally {
                redisLockService.releaseLock(MARKET_CLOSE_LOCK);
            }
        }
    }

}