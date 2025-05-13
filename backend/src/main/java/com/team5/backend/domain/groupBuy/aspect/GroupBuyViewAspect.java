package com.team5.backend.domain.groupBuy.aspect;

import com.team5.backend.domain.category.entity.Category;
import com.team5.backend.domain.category.entity.KeywordType;
import com.team5.backend.domain.groupBuy.entity.GroupBuy;
import com.team5.backend.domain.groupBuy.repository.GroupBuyRepository;
import com.team5.backend.domain.product.entity.Product;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;


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
    @After("onGroupBuyView()")
    public void afterGroupBuyViewed(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        if (args.length == 0 || !(args[0] instanceof Long)) return;

        Long groupBuyId = (Long) args[0];
        GroupBuy groupBuy = groupBuyRepository.findById(groupBuyId)
                .orElse(null);
        if (groupBuy == null || groupBuy.getProduct() == null) {
            log.warn("GroupBuy or Product not found for ID: {}", groupBuyId);
            return;
        }

        Product product = groupBuy.getProduct();
        Category category = product.getCategory();
        KeywordType keyword = category.getKeyword();
        if (keyword != null) {
            redisTemplate.opsForZSet().incrementScore("keyword_rank", keyword.name(), 1.0);
        }

    }

}
