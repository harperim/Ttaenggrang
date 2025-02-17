package com.ladysparks.ttaenggrang.global.redis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisLockService {

    private final RedisTemplate<String, Object> redisTemplate;
    private static final long LOCK_EXPIRE_TIME = 5 * 60; // 5분

    /**
     * Redis Lock 획득
     */
    public boolean acquireLock(String key) {
        Boolean success = redisTemplate.opsForValue().setIfAbsent(key, "LOCK", LOCK_EXPIRE_TIME, TimeUnit.SECONDS);
        if (Boolean.TRUE.equals(success)) {
            log.info("✅ Redis Lock 획득: {}", key);
            return true;
        }
        log.warn("🚫 Redis Lock 이미 점유 중: {}", key);
        return false;
    }

    /**
     * Redis Lock 해제
     */
    public void releaseLock(String key) {
        redisTemplate.delete(key);
        log.info("🔓 Redis Lock 해제: {}", key);
    }

}
