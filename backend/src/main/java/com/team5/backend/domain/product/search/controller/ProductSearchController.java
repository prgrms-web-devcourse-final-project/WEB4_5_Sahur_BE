package com.team5.backend.domain.product.search.controller;

import com.team5.backend.domain.product.search.document.ProductDocument;
import com.team5.backend.domain.product.search.service.ProductSearchService;
import com.team5.backend.global.dto.RsData;
import com.team5.backend.global.exception.RsDataUtil;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products/search")
@RequiredArgsConstructor
public class ProductSearchController {

    private final ProductSearchService productSearchService;

    @Operation(summary = "상품 검색", description = "키워드로 상품을 검색합니다.")
    @GetMapping
    public RsData<List<ProductDocument>> search(@RequestParam String keyword) {
        List<ProductDocument> result = productSearchService.search(keyword);
        return RsDataUtil.success("검색 성공", result);
    }
}