package com.team5.backend.domain.category.service;

import com.team5.backend.domain.category.dto.CategoryCreateReqDto;
import com.team5.backend.domain.category.dto.CategoryResDto;
import com.team5.backend.domain.category.dto.CategoryUpdateReqDto;
import com.team5.backend.domain.category.dto.KeywordResDto;
import com.team5.backend.domain.category.entity.Category;
import com.team5.backend.domain.category.entity.CategoryType;
import com.team5.backend.domain.category.entity.KeywordType;
import com.team5.backend.domain.category.repository.CategoryRepository;
import com.team5.backend.global.exception.CustomException;
import com.team5.backend.global.exception.code.ProductErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    /**
     * 카테고리 생성
     */
    @Transactional
    public CategoryResDto createCategory(CategoryCreateReqDto request) {

        Category category = Category.builder()
                .categoryType(request.getCategoryType())
                .keyword(request.getKeyword())
                .build();

        Category saved = categoryRepository.save(category);
        return CategoryResDto.fromEntity(saved);
    }

    /**
     * 전체 카테고리 목록 조회
     */
    @Transactional(readOnly = true)
    public List<CategoryResDto> getAllCategories() {
        return categoryRepository.findAll(Sort.by("categoryId"))
                .stream()
                .map(CategoryResDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 카테고리 단건 조회
     */
    @Transactional(readOnly = true)
    public CategoryResDto getCategoryById(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CustomException(ProductErrorCode.CATEGORY_NOT_FOUND));
        return CategoryResDto.fromEntity(category);
    }

    /**
     * 카테고리 수정
     */
    @Transactional
    public CategoryResDto updateCategory(Long categoryId, CategoryUpdateReqDto request) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CustomException(ProductErrorCode.CATEGORY_NOT_FOUND));

        category.updateCategoryInfo(request.getCategoryType(), request.getKeyword());
        return CategoryResDto.fromEntity(category);
    }

    /**
     * 카테고리 삭제
     */
    @Transactional
    public void deleteCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CustomException(ProductErrorCode.CATEGORY_NOT_FOUND));

        categoryRepository.delete(category);
    }

    /**
     * 카테고리(대분류)에 속한 키워드(중분류) 목록 조회
     */
    @Transactional(readOnly = true)
    public List<KeywordResDto> getKeywordsByCategory(CategoryType category) {

        // null을 반환하면 NPE 발생 → Exception으로 잡히고 500 발생
        return Optional.ofNullable(KeywordType.ofParent(category))
                .orElse(List.of()) // 빈값 반환
                .stream()
                .map(k -> new KeywordResDto(k.name(), k.getDisplayName()))
                .toList();
    }

}
