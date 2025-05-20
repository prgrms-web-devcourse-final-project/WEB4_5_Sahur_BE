package com.team5.backend.domain.member.productrequest.controller;

import com.team5.backend.domain.member.productrequest.dto.ProductRequestCreateReqDto;
import com.team5.backend.domain.member.productrequest.dto.ProductRequestResDto;
import com.team5.backend.domain.member.productrequest.service.ProductRequestService;
import com.team5.backend.global.dto.RsData;
import com.team5.backend.global.exception.RsDataUtil;
import com.team5.backend.global.security.PrincipalDetails;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "ProductRequest", description = "사용자 상품 등록 요청 API")
@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductRequestController {

    private final ProductRequestService productRequestService;

    @Operation(summary = "상품 등록 요청 생성", description = "로그인한 사용자가 상품 등록 요청을 생성합니다.")
    @PostMapping("/request")
    public RsData<ProductRequestResDto> createRequest(
            @RequestBody @Valid ProductRequestCreateReqDto request,
            @AuthenticationPrincipal PrincipalDetails userDetails
    ) {
        ProductRequestResDto response = productRequestService.createRequest(request, userDetails);
        return RsDataUtil.success("상품 등록 요청이 생성되었습니다.", response);
    }
}
