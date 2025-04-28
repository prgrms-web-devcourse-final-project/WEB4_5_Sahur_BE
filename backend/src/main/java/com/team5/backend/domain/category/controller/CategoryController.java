package com.team5.backend.domain.category.controller;

import com.team5.backend.domain.category.dto.CategoryCreateReqDto;
import com.team5.backend.domain.category.dto.CategoryResDto;
import com.team5.backend.domain.category.dto.CategoryUpdateReqDto;
import com.team5.backend.domain.category.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<CategoryResDto> createCategory(@RequestBody CategoryCreateReqDto request) {
        CategoryResDto response = categoryService.createCategory(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<CategoryResDto>> getAllCategories() {
        List<CategoryResDto> responses = categoryService.getAllCategories();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<CategoryResDto> getCategoryById(@PathVariable Integer categoryId) {
        CategoryResDto response = categoryService.getCategoryById(categoryId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{categoryId}")
    public ResponseEntity<CategoryResDto> updateCategory(@PathVariable Integer categoryId,
                                                         @RequestBody CategoryUpdateReqDto request) {
        CategoryResDto response = categoryService.updateCategory(categoryId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Integer categoryId) {
        categoryService.deleteCategory(categoryId);
        return ResponseEntity.noContent().build();
    }
}
