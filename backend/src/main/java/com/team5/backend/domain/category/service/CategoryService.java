package com.team5.backend.domain.category.service;

import com.team5.backend.domain.category.dto.CategoryCreateReqDto;
import com.team5.backend.domain.category.dto.CategoryResDto;
import com.team5.backend.domain.category.dto.CategoryUpdateReqDto;
import com.team5.backend.domain.category.entity.Category;
import com.team5.backend.domain.category.repository.CategoryRepository;
import com.team5.backend.domain.product.repository.ProductRepository;
import com.team5.backend.global.exception.CustomException;
import com.team5.backend.global.exception.code.ProductErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    /**
     * 카테고리 생성
     */
    public CategoryResDto createCategory(CategoryCreateReqDto request) {

        Category category = Category.builder()
                .category(request.getCategory())
                .keyword(request.getKeyword())
                .uid(request.getUid())
                .build();

        Category saved = categoryRepository.save(category);
        return CategoryResDto.fromEntity(saved);
    }

    /**
     * 전체 카테고리 목록 조회
     */
    public List<CategoryResDto> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(CategoryResDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 카테고리 단건 조회
     */
    public CategoryResDto getCategoryById(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CustomException(ProductErrorCode.CATEGORY_NOT_FOUND));
        return CategoryResDto.fromEntity(category);
    }

    /**
     * 카테고리 수정
     */
    public CategoryResDto updateCategory(Long categoryId, CategoryUpdateReqDto request) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CustomException(ProductErrorCode.CATEGORY_NOT_FOUND));

        category.updateCategoryInfo(request.getCategory(), request.getKeyword(), request.getUid());

        Category updated = categoryRepository.save(category);
        return CategoryResDto.fromEntity(updated);
    }

    /**
     * 카테고리 삭제
     */
    public void deleteCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CustomException(ProductErrorCode.CATEGORY_NOT_FOUND));

        categoryRepository.delete(category);
    }

}
