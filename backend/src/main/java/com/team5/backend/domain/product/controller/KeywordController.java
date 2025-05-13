package com.team5.backend.domain.product.controller;

import com.team5.backend.domain.product.dto.KeywordResDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/keywords")
@RequiredArgsConstructor
public class KeywordController {

    private final StringRedisTemplate redisTemplate;

    @GetMapping("/popular")
    public List<KeywordResDto> getPopularKeywords(@RequestParam(defaultValue = "10") int limit) {
        Set<ZSetOperations.TypedTuple<String>> results =
                redisTemplate.opsForZSet().reverseRangeWithScores("keyword_rank", 0, limit - 1);

        if (results == null) return Collections.emptyList();

        return results.stream()
                .map(tuple -> new KeywordResDto(tuple.getValue(), tuple.getScore()))
                .collect(Collectors.toList());
    }
}
