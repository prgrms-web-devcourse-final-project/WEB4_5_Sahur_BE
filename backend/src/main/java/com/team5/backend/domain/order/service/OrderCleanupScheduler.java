package com.team5.backend.domain.order.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderCleanupScheduler {

    private final OrderService orderService;

    @Scheduled(fixedRate = 60 * 60 * 1000) // 1시간마다 실행
    public void deleteStaleBeforePaidOrders() {
        log.info("[Order Cleanup] 주문 정리 작업 시작");
        orderService.deleteExpiredOrders();
        log.info("[Order Cleanup] 주문 정리 작업 종료");
    }
}
