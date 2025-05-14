package com.team5.backend.domain.product.repository;

import com.team5.backend.domain.category.entity.CategoryType;
import com.team5.backend.domain.category.entity.KeywordType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.team5.backend.domain.product.entity.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, ProductRepositoryCustom  {
	Page<Product> findByCategory_CategoryType(CategoryType categoryType, Pageable pageable);	// 카테고리 필드 기준 검색
	Page<Product> findByCategory_Keyword(KeywordType keyword, Pageable pageable);	// 키워드 필드 기준 검색
}
