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

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class GroupBuyViewAspect {

    private final GroupBuyRepository groupBuyRepository;
    private final StringRedisTemplate redisTemplate;

    @Pointcut("execution(* *..GroupBuyController.getGroupBuyById(..))")
    public void onGroupBuyView() {}

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
