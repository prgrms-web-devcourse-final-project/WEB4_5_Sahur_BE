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

    @Convert(converter = StringListConverter.class)
    @Column(name = "imageUrl", columnDefinition = "TEXT")
    private List<String> imageUrl;

    @Column(nullable = false)
    private Integer price;

    @Column
    private Long dibCount;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private Product(Category category, String title, String description, List<String> imageUrl, Integer price) {
        this.category = category;
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;
        this.price = price;
        this.dibCount = 0L;
        this.createdAt = LocalDateTime.now();
    }

    public static Product create(Category category, String title, String description, List<String> imageUrl, Integer price) {
        return new Product(category, title, description, imageUrl, price);
    }

    public void update(String title, String description, List<String> imageUrl, Integer price) {

        if (title != null) this.title = title;
        if (description != null) this.description = description;
        if (imageUrl != null) this.imageUrl = imageUrl;
        if (price != null) this.price = price;
    }

    public void updateCategory(Category category) {
        this.category = category;
    }

}

