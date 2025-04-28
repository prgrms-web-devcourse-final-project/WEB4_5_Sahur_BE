package com.team5.backend.domain.groupBuy.entity;

import com.team5.backend.domain.product.entity.Product;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "GroupBuy")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupBuy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long groupBuyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "productId", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoryId", nullable = false)
    private Category category;

    @Column
    private Integer minParticipants = 0;

    @Column
    private Integer currentParticipants = 0;

    @Column
    private Integer round = 0;

    @Column(nullable = false)
    private LocalDateTime deadline = null;

    @Enumerated(EnumType.STRING)
    @Column
    private GroupBuyStatus status;


}
