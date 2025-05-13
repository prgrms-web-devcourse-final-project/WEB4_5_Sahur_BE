package com.team5.backend.domain.groupBuy.aspect;

import com.team5.backend.domain.category.entity.Category;
import com.team5.backend.domain.category.entity.KeywordType;
import com.team5.backend.domain.groupBuy.entity.GroupBuy;
import com.team5.backend.domain.groupBuy.repository.GroupBuyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


/**
 * 사용자가 공동구매 상세 페이지에 진입할 때 호출되는 AOP 클래스.
 * 해당 공동구매에 연결된 상품의 키워드를 Redis에 카운팅하여 인기 키워드 통계를 생성한다.
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class GroupBuyViewAspect {

    private final GroupBuyRepository groupBuyRepository;
    private final StringRedisTemplate redisTemplate;

    /**
     * 공동구매 상세 조회 컨트롤러 메서드 실행 후 수행될 AOP 포인트컷.
     */
    @Pointcut("execution(* *..GroupBuyController.getGroupBuyById(..))")
    public void onGroupBuyView() {}

    /**
     * 공동구매 조회 후 반환 시 상품의 카테고리 키워드를 기준으로 Redis의 Sorted Set에 점수 1점 추가.
     * ex) ZINCRBY keyword_rank 1 "강아지간식"
     */
    @AfterReturning(pointcut = "onGroupBuyView()", returning = "result")
    public void afterGroupBuyViewed(JoinPoint joinPoint, Object result) {
        Object[] args = joinPoint.getArgs();
        if (args.length == 0 || !(args[0] instanceof Long)) return;

        Long groupBuyId = (Long) args[0];
        GroupBuy groupBuy = groupBuyRepository.findById(groupBuyId).orElse(null);
        if (groupBuy == null || groupBuy.getProduct() == null) return;

        Category category = groupBuy.getProduct().getCategory();
        KeywordType keyword = category.getKeyword();
        if (keyword == null) return;

        // 현재 시각 기준 시간별 키 생성
        String hourKey = "keyword_rank:" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHH"));

        redisTemplate.opsForZSet().incrementScore(hourKey, keyword.name(), 1.0);

        // Redis에서 2시간 뒤 자동 삭제 설정
        redisTemplate.expire(hourKey, Duration.ofHours(2));
    }


}
