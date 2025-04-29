package com.team5.backend.domain.history.entity;

import com.team5.backend.domain.groupBuy.entity.GroupBuy;
import com.team5.backend.domain.member.member.entity.Member;
import com.team5.backend.domain.order.entity.Order;
import com.team5.backend.domain.product.entity.Product;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class History {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long historyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memberId", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "productId", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "groupBuyId", nullable = false)
    private GroupBuy groupBuy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orderId", nullable = false)
    private Order order;

    @Column(nullable = false)
    private Boolean writable = false; // 작성 가능 여부

    @CreatedDate
    private LocalDateTime createdAt;
}