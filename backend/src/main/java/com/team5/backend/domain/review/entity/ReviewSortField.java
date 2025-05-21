package com.team5.backend.domain.review.entity;

import org.springframework.data.domain.Sort;

public enum ReviewSortField {
    LATEST,  // 최신순
    RATE, // 평점순
    OLDEST; // 오래된순

    public Sort toSort() {
        return switch (this) {
            case RATE -> Sort.by(Sort.Order.desc("rate"), Sort.Order.desc("createdAt"));
            case LATEST -> Sort.by(Sort.Order.desc("createdAt"));
            case OLDEST -> Sort.by(Sort.Order.asc("createdAt"));
        };
    }
}

