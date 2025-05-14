package com.team5.backend.domain.history.controller;

import com.team5.backend.domain.history.dto.HistoryCreateReqDto;
import com.team5.backend.domain.history.dto.HistoryResDto;
import com.team5.backend.domain.history.dto.HistoryUpdateReqDto;
import com.team5.backend.domain.history.service.HistoryService;
import com.team5.backend.global.dto.RsData;
import com.team5.backend.global.dto.Empty;
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

import java.util.Collections;
import java.util.Map;

@Tag(name = "History", description = "구매 이력 관련 API")
@RestController
@RequestMapping("/api/v1/histories")
@RequiredArgsConstructor
public class HistoryController {

    private final HistoryService historyService;

    @Operation(summary = "구매 이력 생성", description = "구매 이력을 생성합니다.")
    @PostMapping
    public RsData<HistoryResDto> createHistory(
            @AuthenticationPrincipal PrincipalDetails userDetails,
            @RequestBody HistoryCreateReqDto request) {

        HistoryResDto response = historyService.createHistory(request, userDetails);
        return RsDataUtil.success("구매 이력이 생성되었습니다.", response);
    }

    @Operation(summary = "전체 구매 이력 조회", description = "모든 구매 이력을 조회합니다 (최신순 정렬).")
    @GetMapping
    public RsData<Page<HistoryResDto>> getAllHistories(
            @Parameter(description = "페이지 정보") @PageableDefault(size = 5) Pageable pageable) {

        Page<HistoryResDto> responses = historyService.getAllHistories(pageable);
        return RsDataUtil.success("전체 구매 이력 조회 성공", responses);
    }

    @Operation(summary = "구매 이력 단건 조회", description = "ID로 특정 구매 이력을 조회합니다.")
    @GetMapping("/{id}")
    public RsData<HistoryResDto> getHistoryById(
            @Parameter(description = "구매 이력 ID") @PathVariable Long id) {

        HistoryResDto dto = historyService.getHistoryById(id);
        return RsDataUtil.success("구매 이력 조회 성공", dto);
    }

    @Operation(summary = "구매 이력 수정", description = "구매 이력의 writable 값을 수정합니다.")
    @PutMapping("/{id}")
    public RsData<HistoryResDto> updateHistory(
            @Parameter(description = "구매 이력 ID") @PathVariable Long id,
            @RequestBody HistoryUpdateReqDto request) {

        HistoryResDto response = historyService.updateHistory(id, request);
        return RsDataUtil.success("구매 이력 수정 성공", response);
    }

    @Operation(summary = "구매 이력 삭제", description = "구매 이력을 삭제합니다.")
    @DeleteMapping("/{id}")
    public RsData<Empty> deleteHistory(
            @Parameter(description = "구매 이력 ID") @PathVariable Long id) {

        historyService.deleteHistory(id);
        return RsDataUtil.success("구매 이력 삭제 완료");
    }

    @Operation(summary = "리뷰 작성 가능 여부 조회", description = "특정 상품에 대해 리뷰 작성 가능 여부를 조회합니다.")
    @GetMapping("/products/{productId}/writable")
    public RsData<Map<String, Boolean>> isReviewWritable(
            @AuthenticationPrincipal PrincipalDetails userDetails,
            @PathVariable Long productId) {

        boolean writable = historyService.checkReviewWritable(productId, userDetails);
        Map<String, Boolean> result = Collections.singletonMap("writable", writable);
        return RsDataUtil.success("리뷰 작성 가능 여부 조회 성공", result);
    }
}
