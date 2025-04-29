package com.team5.backend.domain.member.admin.entity;

import com.team5.backend.domain.member.member.entity.Member;
import com.team5.backend.domain.product.entity.Product;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "GroupBuyRequest")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)

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

    @CreatedDate
    private LocalDateTime createdAt;
}
