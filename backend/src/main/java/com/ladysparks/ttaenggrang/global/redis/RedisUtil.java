package com.ladysparks.ttaenggrang.global.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class RedisUtil {

    private final RedisTemplate<String, String> redisTemplate;

    /** 키 존재 여부 확인 */
    public boolean hasKey(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    /** 특정 키의 TTL 조회 */
    public long getTTL(String key) {
        return redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }

}
