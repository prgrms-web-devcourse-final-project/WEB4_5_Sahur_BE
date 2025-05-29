package com.team5.backend.domain.notification.redis;

import java.util.List;

import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team5.backend.domain.groupBuy.repository.GroupBuyRepository;
import com.team5.backend.domain.member.member.entity.Member;
import com.team5.backend.domain.member.member.repository.MemberRepository;
import com.team5.backend.domain.member.productrequest.repository.ProductRequestRepository;
import com.team5.backend.domain.notification.entity.Notification;
import com.team5.backend.domain.notification.repository.NotificationRepository;
import com.team5.backend.domain.notification.template.NotificationTemplateFactory;
import com.team5.backend.domain.notification.template.NotificationTemplateType;
import com.team5.backend.domain.order.repository.OrderRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class NotificationSubscriber implements MessageListener {

    private final ObjectMapper objectMapper;
    private final NotificationTemplateFactory templateFactory;
    private final NotificationRepository notificationRepository;
    private final OrderRepository orderRepository;
    private final GroupBuyRepository groupBuyRepository;
    private final ProductRequestRepository productRequestRepository;
    private final MemberRepository memberRepository;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            NotificationEventMessage event = objectMapper.readValue(message.getBody(), NotificationEventMessage.class);
            NotificationTemplateType type = event.type();

            Object target = switch (type) {
                case PURCHASED, ORDER_CANCELED, IN_DELIVERY, DELIVERY_DONE ->
                        orderRepository.findById(event.resourceId()).orElse(null);
                case REQUEST_APPROVED, REQUEST_REJECTED ->
                        productRequestRepository.findById(event.resourceId()).orElse(null);
                case GROUP_CLOSED, DIBS_REOPENED, DIBS_DEADLINE ->
                        groupBuyRepository.findById(event.resourceId()).orElse(null);
            };

            if (target == null) {
                throw new IllegalArgumentException("알림 대상이 존재하지 않습니다: " + event);
            }

            List<Member> members = (event.memberIds() != null)
                    ? memberRepository.findAllById(event.memberIds())
                    : null;

            List<Notification> notifications = templateFactory.createAll(
                    type, target, members, event.adminMessage()
            );

            notificationRepository.saveAll(notifications);

        } catch (Exception e) {
            log.error("[Redis 알림 수신 실패]", e);
        }
    }
}
