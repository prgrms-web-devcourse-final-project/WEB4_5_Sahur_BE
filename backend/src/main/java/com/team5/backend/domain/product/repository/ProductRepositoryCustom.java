package com.team5.backend.domain.product.repository;

import com.team5.backend.domain.product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductRepositoryCustom {
    Page<Product> findAllByFilter(String categoryName, String keywordName, Pageable pageable);
}