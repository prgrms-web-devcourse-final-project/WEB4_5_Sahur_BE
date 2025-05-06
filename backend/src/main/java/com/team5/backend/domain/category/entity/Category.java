package com.team5.backend.domain.category.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "categories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long categoryId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CategoryType category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private KeywordType keyword;

    @Column(nullable = false)
    private Integer uid;

    public void updateCategoryInfo(CategoryType category, KeywordType keyword, Integer uid) {
        this.category = category;
        this.keyword = keyword;
        this.uid = uid;
    }
}
