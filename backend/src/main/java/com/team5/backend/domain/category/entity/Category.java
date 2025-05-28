package com.team5.backend.domain.category.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "categories")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long categoryId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CategoryType categoryType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private KeywordType keyword;

    public void updateCategoryInfo(CategoryType categoryType, KeywordType keyword) {
        this.categoryType = categoryType;
        this.keyword = keyword;
    }
}
