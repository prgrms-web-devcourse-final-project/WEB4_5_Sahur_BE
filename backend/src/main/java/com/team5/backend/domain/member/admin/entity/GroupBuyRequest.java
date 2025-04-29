package com.team5.backend.domain.member.admin.entity;

import com.team5.backend.domain.member.member.entity.Member;
import com.team5.backend.domain.product.entity.Product;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "GroupBuyRequest")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupBuyRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long groupBuyRequestId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "productId", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memberId", nullable = false)
    private Member member;
}
