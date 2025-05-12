package com.team5.backend.domain.category.service;

import com.team5.backend.domain.category.dto.CategoryCreateReqDto;
import com.team5.backend.domain.category.dto.CategoryResDto;
import com.team5.backend.domain.category.dto.CategoryUpdateReqDto;
import com.team5.backend.domain.category.entity.Category;
import com.team5.backend.domain.category.repository.CategoryRepository;
import com.team5.backend.global.exception.CustomException;
import com.team5.backend.global.exception.code.ProductErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    private Category sample1;
    private Category sample2;
    private List<Category> sampleList;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        sample1 = Category.builder()
                .categoryId(1L)
                .category(null)
                .keyword(null)
                .uid(101)
                .build();

        sample2 = Category.builder()
                .categoryId(2L)
                .category(null)
                .keyword(null)
                .uid(102)
                .build();

        sampleList = List.of(sample1, sample2);
    }


    @Test
    @DisplayName("카테고리 생성 - 성공")
    void createCategory() {
        CategoryCreateReqDto req = CategoryCreateReqDto.builder()
                .category(null)
                .keyword(null)
                .uid(103)
                .build();

        Category saved = Category.builder()
                .categoryId(3L)
                .category(null)
                .keyword(null)
                .uid(103)
                .build();

        when(categoryRepository.save(any(Category.class))).thenReturn(saved);

        CategoryResDto dto = categoryService.createCategory(req);

        assertNotNull(dto);
        assertEquals(3L, dto.getCategoryId());
        assertEquals(103, dto.getUid());
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    @DisplayName("전체 카테고리 조회 - 성공")
    void getAllCategories() {
        when(categoryRepository.findAll()).thenReturn(sampleList);

        List<CategoryResDto> result = categoryService.getAllCategories();

        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getCategoryId());
        assertEquals(2L, result.get(1).getCategoryId());
        verify(categoryRepository).findAll();
    }

    @Test
    @DisplayName("단건 카테고리 조회 - 존재할 때")
    void getCategoryById() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(sample1));

        CategoryResDto dto = categoryService.getCategoryById(1L);

        assertNotNull(dto);
        assertEquals(1L, dto.getCategoryId());
        verify(categoryRepository).findById(1L);
    }

    @Test
    @DisplayName("카테고리 수정 - 성공")
    void updateCategory() {
        CategoryUpdateReqDto req = CategoryUpdateReqDto.builder()
                .category(null)
                .keyword(null)
                .uid(110)
                .build();

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(sample1));

        CategoryResDto dto = categoryService.updateCategory(1L, req);

        assertNotNull(dto);
        assertEquals(110, dto.getUid());
        verify(categoryRepository).findById(1L);
    }

    @Test
    @DisplayName("카테고리 삭제 - 성공")
    void deleteCategory() {
        when(categoryRepository.findById(2L)).thenReturn(Optional.of(sample2));

        assertDoesNotThrow(() -> categoryService.deleteCategory(2L));
        verify(categoryRepository).delete(sample2);
    }

    @Test
    @DisplayName("단건 카테고리 조회 - 없을 때 예외")
    void getCategoryById_notFound() {
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        CustomException ex = assertThrows(
                CustomException.class,
                () -> categoryService.getCategoryById(99L)
        );
        assertEquals(ProductErrorCode.CATEGORY_NOT_FOUND, ex.getErrorCode());
        verify(categoryRepository).findById(99L);
    }

    @Test
    @DisplayName("카테고리 수정 - 없을 때 예외")
    void updateCategory_notFound() {
        when(categoryRepository.findById(50L)).thenReturn(Optional.empty());

        CustomException ex = assertThrows(
                CustomException.class,
                () -> categoryService.updateCategory(50L,
                        CategoryUpdateReqDto.builder()
                                .category(null)
                                .keyword(null)
                                .uid(0)
                                .build()
                )
        );
        assertEquals(ProductErrorCode.CATEGORY_NOT_FOUND, ex.getErrorCode());
        verify(categoryRepository).findById(50L);
    }

    @Test
    @DisplayName("카테고리 삭제 - 없을 때 예외")
    void deleteCategory_notFound() {
        when(categoryRepository.findById(3L)).thenReturn(Optional.empty());

        CustomException ex = assertThrows(
                CustomException.class,
                () -> categoryService.deleteCategory(3L)
        );
        assertEquals(ProductErrorCode.CATEGORY_NOT_FOUND, ex.getErrorCode());
        verify(categoryRepository).findById(3L);
    }
}