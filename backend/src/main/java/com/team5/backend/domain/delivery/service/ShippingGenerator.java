package com.team5.backend.domain.delivery.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class ShippingGenerator {

    private final StringRedisTemplate redisTemplate;

    public ShippingGenerator(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public String generateShipping() {
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyMMdd"));
        String redisKey = "shipping:seq:" + date;

        Long sequence = redisTemplate.opsForValue().increment(redisKey);
        if (sequence != null && sequence == 1L) {
            redisTemplate.expire(redisKey, java.time.Duration.ofDays(1));
        }

        String sequenceStr = String.format("%04d", sequence);
        return "TRK" + date + sequenceStr;
    }
}
