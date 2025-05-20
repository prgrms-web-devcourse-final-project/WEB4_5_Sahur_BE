package com.team5.backend.domain.dibs.entity;

import com.team5.backend.domain.member.member.entity.Member;
import com.team5.backend.domain.product.entity.Product;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

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

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

}
