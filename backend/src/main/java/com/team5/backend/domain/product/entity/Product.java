package com.team5.backend.domain.product.entity;

import com.team5.backend.domain.category.entity.Category;
import com.team5.backend.domain.product.converter.StringListConverter;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "product")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoryId", nullable = false)
    private Category category;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @Column(name = "imageUrl")
    @Convert(converter = StringListConverter.class)
    private List<String> imageUrl;

    @Column(nullable = false)
    private Integer price;

    @Column
    private Long dibCount;

    @Column(nullable = false)
    private LocalDateTime createdAt;

}

