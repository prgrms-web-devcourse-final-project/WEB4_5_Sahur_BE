package com.team5.backend.domain.category.service;

import com.team5.backend.domain.category.dto.CategoryCreateReqDto;
import com.team5.backend.domain.category.dto.CategoryResDto;
import com.team5.backend.domain.category.dto.CategoryUpdateReqDto;
import com.team5.backend.domain.category.entity.Category;
import com.team5.backend.domain.category.repository.CategoryRepository;
import com.team5.backend.domain.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    public CategoryResDto createCategory(CategoryCreateReqDto request) {

        Category category = Category.builder()
                .category(request.getCategory())
                .keyword(request.getKeyword())
                .uid(request.getUid())
                .build();

        Category saved = categoryRepository.save(category);
        return CategoryResDto.fromEntity(saved);
    }

    public List<CategoryResDto> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(CategoryResDto::fromEntity)
                .collect(Collectors.toList());
    }

    public CategoryResDto getCategoryById(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found with id " + categoryId));
        return CategoryResDto.fromEntity(category);
    }

    public CategoryResDto updateCategory(Long categoryId, CategoryUpdateReqDto request) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found with id " + categoryId));

        category.updateCategoryInfo(request.getCategory(), request.getKeyword(), request.getUid());

        Category updated = categoryRepository.save(category);
        return CategoryResDto.fromEntity(updated);
    }

    public void deleteCategory(Long categoryId) {
        categoryRepository.deleteById(categoryId);
    }

}
