package com.team5.backend.domain.product.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.team5.backend.domain.product.entity.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
	List<Product> findByCategory_Category(String category);	// 카테고리 필드 기준 검색
	List<Product> findByCategory_Keyword(String keyword);	// 키워드 필드 기준 검색
}
