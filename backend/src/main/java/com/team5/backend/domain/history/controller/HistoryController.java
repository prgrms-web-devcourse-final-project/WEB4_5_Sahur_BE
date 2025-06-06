package com.team5.backend.domain.history.controller;

import com.team5.backend.domain.history.dto.HistoryCreateReqDto;
import com.team5.backend.domain.history.dto.HistoryResDto;
import com.team5.backend.domain.history.dto.HistoryUpdateReqDto;
import com.team5.backend.domain.history.service.HistoryService;
import com.team5.backend.global.annotation.CheckAdmin;
import com.team5.backend.global.dto.Empty;
import com.team5.backend.global.dto.RsData;
import com.team5.backend.global.exception.CustomException;
import com.team5.backend.global.exception.RsDataUtil;
import com.team5.backend.global.exception.code.CommonErrorCode;
import com.team5.backend.global.security.PrincipalDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "History", description = "구매 이력 관련 API")
@RestController
@RequestMapping("/api/v1/histories")
@RequiredArgsConstructor
public class HistoryController {

    private final HistoryService historyService;

    @Operation(summary = "구매 이력 생성", description = "구매 이력을 생성합니다.")
    @CheckAdmin
    @PostMapping
    public RsData<HistoryResDto> createHistory(
            @AuthenticationPrincipal PrincipalDetails userDetails,
            @Valid @RequestBody HistoryCreateReqDto request) {

        HistoryResDto response = historyService.createHistory(request, userDetails);
        return RsDataUtil.success("구매 이력이 생성되었습니다.", response);
    }

    @Operation(summary = "전체 구매 이력 조회", description = "모든 구매 이력을 조회합니다 (최신순 정렬).")
    @CheckAdmin
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
            @Valid @RequestBody HistoryUpdateReqDto request) {

        HistoryResDto response = historyService.updateHistory(id, request);
        return RsDataUtil.success("구매 이력 수정 성공", response);
    }

    @Operation(summary = "구매 이력 삭제", description = "구매 이력을 삭제합니다.")
    @CheckAdmin
    @DeleteMapping("/{id}")
    public RsData<Empty> deleteHistory(
            @Parameter(description = "구매 이력 ID") @PathVariable Long id) {

        historyService.deleteHistory(id);
        return RsDataUtil.success("구매 이력 삭제 완료");
    }

    @Operation(summary = "리뷰 작성 가능한 구매 내역 조회", description = "특정 상품에 대해 리뷰 작성 가능한 구매 이력 목록을 반환합니다.")
    @GetMapping("/products/{productId}/writable-histories")
    public RsData<List<HistoryResDto>> getWritableHistories(
            @AuthenticationPrincipal PrincipalDetails userDetails,
            @PathVariable Long productId) {

        if (userDetails == null) {
            throw new CustomException(CommonErrorCode.UNAUTHORIZED);
        }

        List<HistoryResDto> writableHistories = historyService.getWritableHistories(productId, userDetails);
        return RsDataUtil.success("리뷰 작성 가능한 구매 이력 조회 성공", writableHistories);
    }

    @Operation(summary = "리뷰 작성 가능한 내 구매 이력 조회 (최신순, 페이징)", description = "현재 로그인한 사용자의 writable = true 인 구매 이력을 최신순으로 페이징 조회합니다.")
    @GetMapping("/writable")
    public RsData<Page<HistoryResDto>> getMyWritableHistories(
            @AuthenticationPrincipal PrincipalDetails userDetails,
            @Parameter(description = "페이지 번호 (0부터 시작)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "5") int size) {

        Pageable sortedPageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc("createdAt")));

        Page<HistoryResDto> writableHistories = historyService.getMyWritableHistories(userDetails, sortedPageable);
        return RsDataUtil.success("리뷰 작성 가능한 구매 이력 페이징 조회 성공", writableHistories);
    }





}
