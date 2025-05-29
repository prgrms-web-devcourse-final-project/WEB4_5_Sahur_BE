package com.team5.backend.domain.notification.template;

import java.util.List;

import org.springframework.stereotype.Component;

import com.team5.backend.domain.dibs.entity.Dibs;
import com.team5.backend.domain.groupBuy.entity.GroupBuy;
import com.team5.backend.domain.member.member.entity.Member;
import com.team5.backend.domain.member.productrequest.entity.ProductRequest;
import com.team5.backend.domain.notification.entity.Notification;
import com.team5.backend.domain.notification.entity.NotificationType;
import com.team5.backend.domain.order.entity.Order;
import com.team5.backend.domain.product.entity.Product;

@Component
public class NotificationTemplateFactory {

    public List<Notification> createAll(NotificationTemplateType type, Object payload, List<Member> members, Long groupBuyId, String adminMessage) {
        return switch (type) {
            case PURCHASED -> {
                Order order = (Order) payload;
                yield List.of(build(order.getMember(), NotificationType.ORDER, "구매 완료 알림",
                        "[" + order.getProduct().getTitle() + "] 상품의 구매가 완료되었습니다.",
                        "/mypage/orders/" + order.getOrderId()));
            }
            case ORDER_CANCELED -> {
                Order order = (Order) payload;
                yield List.of(build(order.getMember(), NotificationType.ORDER, "주문 취소 알림",
                        "[" + order.getProduct().getTitle() + "] 상품의 주문이 취소되었습니다.",
                        "/mypage/orders/" + order.getOrderId()));
            }
            case IN_DELIVERY -> {
                Order order = (Order) payload;
                yield List.of(build(order.getMember(), NotificationType.ORDER, "배송 중 알림",
                        "[" + order.getProduct().getTitle() + "] 상품이 배송 중입니다.",
                        "/mypage/orders/" + order.getOrderId()));
            }
            case DELIVERY_DONE -> {
                Order order = (Order) payload;
                yield List.of(build(order.getMember(), NotificationType.ORDER, "배송 완료 알림",
                        "[" + order.getProduct().getTitle() + "] 상품의 배송이 완료되었습니다.",
                        "/mypage/orders/" + order.getOrderId()));
            }
            case REQUEST_APPROVED -> {
                ProductRequest request = (ProductRequest) payload;
                yield List.of(build(request.getMember(), NotificationType.REQUEST, "상품 요청 승인",
                        "[" + request.getTitle() + "] 상품 요청이 승인되었습니다.",
                        "/mypage/requests/me"));
            }
            case REQUEST_REJECTED -> {
                ProductRequest request = (ProductRequest) payload;
                String message = (adminMessage != null)
                        ? "[" + request.getTitle() + "] 상품 요청이 반려되었습니다. 사유: " + adminMessage
                        : "[" + request.getTitle() + "] 상품 요청이 반려되었습니다.";
                yield List.of(build(request.getMember(), NotificationType.REQUEST,
                        "상품 요청 반려", message, "/mypage/requests/me"));
            }
            case DIBS_REOPENED -> {
                Dibs dibs = (Dibs) payload;
                Product product = dibs.getProduct();
                yield members.stream()
                        .map(member -> build(member, NotificationType.DIBS,
                                "공동구매 재오픈",
                                "관심 상품으로 등록한 [" + product.getTitle() + "] 상품의 공동구매가 다시 시작되었습니다.",
                                "/groupBuy/" + groupBuyId))
                        .toList();
            }
            case DIBS_DEADLINE -> {
                Dibs dibs = (Dibs) payload;
                Product product = dibs.getProduct();
                yield members.stream()
                        .map(member -> build(member, NotificationType.DIBS,
                                "공동구매 마감 임박",
                                "관심 상품으로 등록한 [" + product.getTitle() + "] 공동구매가 1시간 뒤 마감됩니다.",
                                "/groupBuy/" + groupBuyId))
                        .toList();
            }
            case GROUP_CLOSED -> {
                GroupBuy group = (GroupBuy) payload;
                String message = (adminMessage != null)
                        ? "[" + group.getProduct().getTitle() + "] 상품의 공동구매가 종료되었습니다. 사유: " + adminMessage
                        : "[" + group.getProduct().getTitle() + "] 상품의 공동구매가 종료되었습니다.";
                yield members.stream()
                        .map(member -> build(member, NotificationType.GROUP_BUY,
                                "공동구매 종료 알림", message, "/groupBuy/" + group.getGroupBuyId()))
                        .toList();
            }
        };
    }

    private Notification build(Member member, NotificationType type, String title, String message, String url) {
        return Notification.builder()
                .member(member)
                .type(type)
                .title(title)
                .message(message)
                .url(url)
                .isRead(false)
                .build();
    }
}
