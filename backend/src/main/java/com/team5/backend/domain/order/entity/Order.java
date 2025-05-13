package com.team5.backend.domain.order.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.team5.backend.domain.delivery.entity.Delivery;
import com.team5.backend.domain.groupBuy.entity.GroupBuy;
import com.team5.backend.domain.member.member.entity.Member;
import com.team5.backend.domain.product.entity.Product;
import com.team5.backend.global.exception.CustomException;
import com.team5.backend.global.exception.code.OrderErrorCode;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "orders")
public class Order {

    @Id
    private Long orderId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memberId", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "groupBuyId", nullable = false)
    private GroupBuy groupBuy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "productId", nullable = false)
    private Product product;

    @OneToOne(mappedBy = "order", fetch = FetchType.LAZY)
    private Delivery delivery;

    @Column(nullable = false)
    private Integer totalPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Column(nullable = false)
    private Integer quantity;

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime createdAt;

    public static Order create(Long orderId, Member member, GroupBuy groupBuy, Product product, Integer quantity) {
        return Order.builder()
                .orderId(orderId)
                .member(member)
                .groupBuy(groupBuy)
                .product(product)
                .totalPrice(product.getPrice() * quantity)
                .status(OrderStatus.BEFOREPAID)
                .quantity(quantity)
                .build();
    }

    public void updateOrderInfo(int quantity, int totalPrice) {
        this.quantity = quantity;
        this.totalPrice = totalPrice;
    }

    public void markAsPaid() {
        if (this.status != OrderStatus.BEFOREPAID) {
            throw new CustomException(OrderErrorCode.INVALID_ORDER_STATUS);
        }
        this.status = OrderStatus.PAID;
    }

    public void markAsCanceled() {
        if (this.status == OrderStatus.CANCELED) {
            throw new CustomException(OrderErrorCode.ORDER_ALREADY_CANCELED);
        }
        this.status = OrderStatus.CANCELED;
    }
    
    public void setDelivery(Delivery delivery) {
        this.delivery = delivery;
    }

}
