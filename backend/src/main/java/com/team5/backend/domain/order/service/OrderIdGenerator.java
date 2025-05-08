package com.team5.backend.domain.order.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
public class OrderIdGenerator {

    private final StringRedisTemplate redisTemplate;

    public OrderIdGenerator(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public Long generateOrderId() {
        // 1. 날짜를 yyMMdd 형식으로 가져오기
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyMMdd"));

        // 2. Redis key 설정
        String redisKey = "order:seq:" + date;

        // 3. Redis INCR → 1부터 증가하는 고유 번호
        Long sequence = redisTemplate.opsForValue().increment(redisKey);

        // 4. 하루 지나면 자동 만료되도록 설정
        if (sequence != null && sequence == 1L) {
            redisTemplate.expire(redisKey, java.time.Duration.ofDays(1));
        }

        // 5. 4자리 시퀀스 포맷팅
        String orderIdStr = date + String.format("%04d", sequence);
        return Long.parseLong(orderIdStr);
    }
}
