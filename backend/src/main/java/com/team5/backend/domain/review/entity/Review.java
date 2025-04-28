package com.team5.backend.domain.review.entity;

import com.team5.backend.domain.member.entity.Member;
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
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reviewId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memberId", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "productId", nullable = false)
    private Product product;

    @Column(length = 255)
    private String comment;

    private Integer rate;

    @CreatedDate
    private LocalDateTime createdAt;

    @Column(columnDefinition = "TEXT")
    private String imageUrl;
}
