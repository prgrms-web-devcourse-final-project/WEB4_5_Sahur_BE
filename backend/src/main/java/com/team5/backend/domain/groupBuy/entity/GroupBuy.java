package com.team5.backend.domain.groupBuy.entity;

import com.team5.backend.domain.category.entity.Category;
import com.team5.backend.domain.product.entity.Product;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "GroupBuy")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class GroupBuy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long groupBuyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "productId", nullable = false)
    private Product product;

    @Column
    private Integer targetParticipants = 0;

    @Column
    private Integer currentParticipantCount = 0;

    @Column
    private Integer round = 0;

    @Column(nullable = false)
    private LocalDateTime deadline = null;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GroupBuyStatus status;

    @CreatedDate
    private LocalDateTime createdAt;


}
