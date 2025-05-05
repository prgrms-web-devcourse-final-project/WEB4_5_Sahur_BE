package com.team5.backend.domain.product.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.team5.backend.domain.product.entity.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
	Page<Product> findByCategory_Category(String category, Pageable pageable);	// 카테고리 필드 기준 검색
	Page<Product> findByCategory_Keyword(String keyword, Pageable pageable);	// 키워드 필드 기준 검색
	Page<Product> findByTitleContainingIgnoreCase(String keyword, Pageable pageable); // 상품명 기준 검색 (대소문자 구분 X)
}
