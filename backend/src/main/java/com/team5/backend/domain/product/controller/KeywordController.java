package com.team5.backend.domain.product.controller;

import com.team5.backend.domain.product.dto.KeywordResDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 인기 키워드 조회 API 컨트롤러.
 * Redis Sorted Set에서 스코어 순으로 상위 키워드를 조회한다.
 */
@RestController
@RequestMapping("/api/v1/keywords")
@RequiredArgsConstructor
public class KeywordController {

    private final StringRedisTemplate redisTemplate;

    /**
     * 인기 키워드 목록 조회
     * @param limit 반환할 키워드 개수 (기본값 10)
     * @return 상위 키워드 리스트 (score 포함)
     */
    @GetMapping("/popular/hourly")
    public List<KeywordResDto> getHourlyPopularKeywords(@RequestParam(defaultValue = "10") int limit) {
        String hourKey = "keyword_rank:" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHH"));
        Set<ZSetOperations.TypedTuple<String>> results = redisTemplate.opsForZSet()
                .reverseRangeWithScores(hourKey, 0, limit - 1);

        if (results == null) return Collections.emptyList();

        return results.stream()
                .map(tuple -> new KeywordResDto(tuple.getValue(), tuple.getScore()))
                .collect(Collectors.toList());
    }

}
