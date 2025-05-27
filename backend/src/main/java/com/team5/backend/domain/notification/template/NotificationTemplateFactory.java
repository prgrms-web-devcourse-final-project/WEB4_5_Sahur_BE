package com.team5.backend.domain.notification.template;

import org.springframework.stereotype.Component;

import com.team5.backend.domain.notification.entity.Notification;
import com.team5.backend.domain.notification.entity.NotificationType;
import com.team5.backend.domain.order.entity.Order;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class NotificationTemplateFactory {

    public Notification create(NotificationTemplateType type, Order order) {
        return switch (type) {
            case PURCHASED -> Notification.builder()
                    .member(order.getMember())
                    .type(NotificationType.ORDER)
                    .title("구매 완료 알림")
                    .message("[" + order.getProduct().getTitle() + "] 상품의 구매가 완료되었습니다.")
                    .url("/orders/" + order.getOrderId())
                    .isRead(false)
                    .build();

            case IN_DELIVERY -> Notification.builder()
                    .member(order.getMember())
                    .type(NotificationType.ORDER)
                    .title("배송 중 알림")
                    .message("[" + order.getProduct().getTitle() + "] 상품이 배송 중입니다.")
                    .url("/orders/" + order.getOrderId())
                    .isRead(false)
                    .build();

            case DELIVERY_DONE -> Notification.builder()
                    .member(order.getMember())
                    .type(NotificationType.ORDER)
                    .title("배송 완료 알림")
                    .message("[" + order.getProduct().getTitle() + "] 상품의 배송이 완료되었습니다.")
                    .url("/orders/" + order.getOrderId())
                    .isRead(false)
                    .build();
        };
    }
}
