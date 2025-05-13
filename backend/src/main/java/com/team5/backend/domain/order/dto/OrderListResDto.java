package com.team5.backend.domain.order.dto;

import java.time.LocalDateTime;

import com.team5.backend.domain.delivery.entity.DeliveryStatus;
import com.team5.backend.domain.order.entity.Order;
import com.team5.backend.domain.order.entity.OrderStatus;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OrderListResDto {
    private Long orderId;
    private Long memberId;
    private String nickname;
    private Long groupBuyId;
    private Long productId;
    private String productTitle;
    private Integer totalPrice;
    private String status;
    private Integer quantity;
    private LocalDateTime createdAt;

    public static OrderListResDto from(Order order) {
        String statusStr;

        if (order.getStatus() == OrderStatus.PAID &&
                order.getDelivery() != null &&
                (order.getDelivery().getStatus() == DeliveryStatus.INDELIVERY ||
                        order.getDelivery().getStatus() == DeliveryStatus.COMPLETED)) {
            // 배송중이거나 배송완료면 그걸로 표시
            statusStr = order.getDelivery().getStatus().name();
        } else {
            // 나머지는 주문 상태로 표시
            statusStr = order.getStatus().name();
        }

        return OrderListResDto.builder()
                .orderId(order.getOrderId())
                .memberId(order.getMember().getMemberId())
                .nickname(order.getMember().getNickname())
                .groupBuyId(order.getGroupBuy().getGroupBuyId())
                .productId(order.getProduct().getProductId())
                .productTitle(order.getProduct().getTitle())
                .totalPrice(order.getTotalPrice())
                .status(statusStr)
                .quantity(order.getQuantity())
                .createdAt(order.getCreatedAt())
                .build();
    }
}