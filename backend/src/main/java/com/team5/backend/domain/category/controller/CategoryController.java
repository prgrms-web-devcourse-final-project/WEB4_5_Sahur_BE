package com.team5.backend.domain.category.controller;

import com.team5.backend.domain.category.dto.CategoryCreateReqDto;
import com.team5.backend.domain.category.dto.CategoryResDto;
import com.team5.backend.domain.category.dto.CategoryUpdateReqDto;
import com.team5.backend.domain.category.dto.KeywordResDto;
import com.team5.backend.domain.category.entity.CategoryType;
import com.team5.backend.domain.category.service.CategoryService;
import com.team5.backend.global.annotation.CheckAdmin;
import com.team5.backend.global.dto.Empty;
import com.team5.backend.global.dto.RsData;
import com.team5.backend.global.exception.RsDataUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Category", description = "카테고리 관련 API")
@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @Operation(summary = "카테고리 생성", description = "새로운 카테고리를 생성합니다.")
    @CheckAdmin
    @PostMapping
    public RsData<CategoryResDto> createCategory(@RequestBody @Valid CategoryCreateReqDto request) {
        CategoryResDto response = categoryService.createCategory(request);
        return RsDataUtil.success("카테고리가 생성되었습니다.", response);
    }

    @Operation(summary = "전체 카테고리 조회", description = "전체 카테고리 목록을 조회합니다.")
    @GetMapping
    public RsData<List<CategoryResDto>> getAllCategories() {
        List<CategoryResDto> responses = categoryService.getAllCategories();
        return RsDataUtil.success("전체 카테고리 조회 성공", responses);
    }

    @Operation(summary = "카테고리 단건 조회", description = "ID를 기반으로 카테고리를 조회합니다.")
    @GetMapping("/{categoryId}")
    public RsData<CategoryResDto> getCategoryById(
            @Parameter(description = "카테고리 ID") @PathVariable Long categoryId) {
        CategoryResDto response = categoryService.getCategoryById(categoryId);
        return RsDataUtil.success("카테고리 조회 성공", response);
    }

    @Operation(summary = "카테고리 수정", description = "카테고리 정보를 수정합니다.")
    @CheckAdmin
    @PutMapping("/{categoryId}")
    public RsData<CategoryResDto> updateCategory(
            @Parameter(description = "카테고리 ID") @PathVariable Long categoryId,
            @RequestBody @Valid CategoryUpdateReqDto request) {
        CategoryResDto response = categoryService.updateCategory(categoryId, request);
        return RsDataUtil.success("카테고리 수정 성공", response);
    }

    @Operation(summary = "카테고리 삭제", description = "카테고리를 삭제합니다.")
    @CheckAdmin
    @DeleteMapping("/{categoryId}")
    public RsData<Empty> deleteCategory(
            @Parameter(description = "카테고리 ID") @PathVariable Long categoryId) {
        categoryService.deleteCategory(categoryId);
        return RsDataUtil.success("카테고리 삭제 성공");
    }

    @Operation(summary = "카테고리(대분류)에 속한 키워드(중분류) 목록 조회",
            description = "주어진 CategoryType(대분류)에 연결된 키워드(중분류) 목록을 반환합니다.")
    @GetMapping("/{category}/keywords")
    public RsData<List<KeywordResDto>> getKeywordsByCategory(
            @Parameter(description = "대분류 카테고리 코드") @PathVariable CategoryType category) {
        List<KeywordResDto> keywords = categoryService.getKeywordsByCategory(category);
        return RsDataUtil.success("중분류(키워드) 목록 조회 성공", keywords);
    }

}
