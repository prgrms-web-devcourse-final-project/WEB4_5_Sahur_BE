package com.team5.backend.domain.review.entity;

import com.team5.backend.domain.history.entity.History;
import com.team5.backend.domain.member.member.entity.Member;
import com.team5.backend.domain.product.converter.StringListConverter;
import com.team5.backend.domain.product.entity.Product;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
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

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "historyId", nullable = false)
    private History history;

    @Column(length = 255)
    private String comment = "";

    private Integer rate = 0;

    @CreatedDate
    private LocalDateTime createdAt;

    @Column(name = "imageUrl", columnDefinition = "TEXT")
    @Convert(converter = StringListConverter.class)
    private List<String> imageUrl;

}
