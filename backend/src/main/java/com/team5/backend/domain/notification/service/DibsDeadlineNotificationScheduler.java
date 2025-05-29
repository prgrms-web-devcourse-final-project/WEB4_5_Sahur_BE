package com.team5.backend.domain.notification.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class DibsDeadlineNotificationScheduler {

    private final NotificationService notificationService;

    /**
     * 공동구매 마감 1시간 전 관심상품 등록자에게 알림 발송
     * 매 5분마다 실행
     */
    @Scheduled(cron = "0 0/5 * * * *")
    public void sendDibsDeadlineNotifications() {
        log.info("[스케줄러 실행] 공동구매 마감 1시간 전 관심상품 알림 발송 시작");

        try {
            notificationService.dibsDeadlineNotifications();
            log.info("[완료] 관심상품 마감 임박 알림 전송 완료");
        } catch (Exception e) {
            log.error("[에러] 관심상품 마감 임박 알림 중 예외 발생", e);
        }
    }
}
