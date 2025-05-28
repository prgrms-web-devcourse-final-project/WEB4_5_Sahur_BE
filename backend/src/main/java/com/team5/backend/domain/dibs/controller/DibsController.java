package com.team5.backend.domain.dibs.controller;

import com.team5.backend.domain.dibs.dto.DibsResDto;
import com.team5.backend.domain.dibs.service.DibsService;
import com.team5.backend.global.dto.Empty;
import com.team5.backend.global.dto.RsData;
import com.team5.backend.global.exception.RsDataUtil;
import com.team5.backend.global.security.PrincipalDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Dibs", description = "관심상품 관련 API")
@RestController
@RequestMapping("/api/v1/dibs")
@RequiredArgsConstructor
public class DibsController {

    private final DibsService dibsService;

    @Operation(summary = "관심상품 등록", description = "회원 인증 기반으로 관심상품을 등록합니다.")
    @PostMapping("/products/{productId}")
    public RsData<DibsResDto> createDibs(
            @AuthenticationPrincipal PrincipalDetails userDetails,
            @PathVariable Long productId) {

        DibsResDto response = dibsService.createDibs(productId, userDetails);
        return RsDataUtil.success("관심상품 등록 완료", response);
    }

    @Operation(summary = "관심상품 삭제", description = "회원 인증 기반으로 관심상품을 삭제합니다.")
    @DeleteMapping("/products/{productId}/dibs")
    public RsData<Empty> deleteDibs(
            @AuthenticationPrincipal PrincipalDetails userDetails,
            @PathVariable Long productId) {

        dibsService.deleteByProductAndMember(productId, userDetails);
        return RsDataUtil.success("관심상품 삭제 완료");
    }

    @Operation(
            summary = "관심상품 목록 조회 (페이징)",
            description = "회원 인증 기반으로 관심상품 목록을 페이징하여 조회하며, 모집중인 공동구매가 있을 경우 함께 포함됩니다."
    )
    @GetMapping
    public RsData<Page<DibsResDto>> getDibsByMember(
            @AuthenticationPrincipal PrincipalDetails userDetails,
            @PageableDefault(size = 6) Pageable pageable) {

        Page<DibsResDto> pagedDibs = dibsService.getPagedDibsByMember(userDetails, pageable);
        return RsDataUtil.success("관심상품 + 모집중 공동구매 페이징 조회 완료", pagedDibs);
    }



}
