package com.team5.backend.domain.dibs.entity;

import com.team5.backend.domain.member.member.entity.Member;
import com.team5.backend.domain.product.entity.Product;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "dibs")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Dibs {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long dibsId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memberId", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "productId", nullable = false)
    private Product product;

}
