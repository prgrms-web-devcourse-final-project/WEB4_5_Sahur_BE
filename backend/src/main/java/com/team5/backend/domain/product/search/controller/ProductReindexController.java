package com.team5.backend.domain.product.search.controller;

import com.team5.backend.domain.product.search.service.ProductReindexService;
import com.team5.backend.global.dto.RsData;
import com.team5.backend.global.exception.RsDataUtil;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/products/reindex")
@RequiredArgsConstructor
@Slf4j
public class ProductReindexController {

    private final ProductReindexService productReindexService;

    @Operation(summary = "상품 재색인", description = "모든 상품을 Elasticsearch에 다시 색인합니다.")
    @PostMapping
    public RsData<Void> reindexAll() {
        log.info("🔁 [재색인 요청] /api/v1/products/reindex 호출됨");
        productReindexService.reindexAll();
        log.info("✅ [재색인 완료]");
        return RsDataUtil.success("재색인 완료", null);
    }
}
