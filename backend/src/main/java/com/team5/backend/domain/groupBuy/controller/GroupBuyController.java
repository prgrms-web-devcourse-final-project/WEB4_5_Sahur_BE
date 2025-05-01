package com.team5.backend.domain.groupBuy.controller;

import com.team5.backend.domain.groupBuy.dto.*;
import com.team5.backend.domain.groupBuy.entity.GroupBuySortField;
import com.team5.backend.domain.groupBuy.service.GroupBuyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "GroupBuy", description = "공동구매 관련 API")
@RestController
@RequestMapping("/api/v1/groupBuy")
@RequiredArgsConstructor
public class GroupBuyController {

    private final GroupBuyService groupBuyService;

    @Operation(summary = "공동구매 생성", description = "신규 공동구매를 생성합니다.")
    @PostMapping
    public ResponseEntity<GroupBuyResDto> createGroupBuy(
            @RequestBody GroupBuyCreateReqDto request) {
        GroupBuyResDto response = groupBuyService.createGroupBuy(request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "전체 공동구매 조회", description = "전체 공동구매 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<Page<GroupBuyResDto>> getAllGroupBuys(
            @Parameter(description = "페이지 정보") @PageableDefault(size = 5) Pageable pageable,
            @Parameter(description = "정렬 기준", example = "LATEST")
            @RequestParam(value = "sortField", defaultValue = "LATEST") GroupBuySortField sortField) {

        Page<GroupBuyResDto> responses = groupBuyService.getAllGroupBuys(pageable, sortField);
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "오늘 마감 공동구매 조회", description = "오늘 마감 예정인 공동구매 목록을 조회합니다.")
    @GetMapping("/closing")
    public ResponseEntity<Page<GroupBuyResDto>> getClosingGroupBuys(
            @Parameter(description = "페이지 정보") @PageableDefault(size = 5) Pageable pageable,
            @Parameter(description = "정렬 기준", example = "LATEST")
            @RequestParam(value = "sortField", defaultValue = "LATEST") GroupBuySortField sortField) {

        Page<GroupBuyResDto> responses = groupBuyService.getTodayDeadlineGroupBuys(pageable, sortField);
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "공동구매 상세 조회", description = "특정 ID의 공동구매 상세 정보를 조회합니다.")
    @GetMapping("/{groupBuyId}")
    public ResponseEntity<GroupBuyResDto> getGroupBuyById(
            @Parameter(description = "공동구매 ID") @PathVariable Long groupBuyId) {
        return groupBuyService.getGroupBuyById(groupBuyId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "공동구매 수정", description = "전체 필드를 수정합니다.")
    @PutMapping("/{groupBuyId}")
    public ResponseEntity<GroupBuyResDto> updateGroupBuy(
            @Parameter(description = "공동구매 ID") @PathVariable Long groupBuyId,
            @Valid @RequestBody GroupBuyUpdateReqDto request) {
        GroupBuyResDto response = groupBuyService.updateGroupBuy(groupBuyId, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "공동구매 부분 수정", description = "특정 필드만 부분적으로 수정합니다.")
    @PatchMapping("/{groupBuyId}")
    public ResponseEntity<GroupBuyResDto> patchGroupBuy(
            @Parameter(description = "공동구매 ID") @PathVariable Long groupBuyId,
            @RequestBody GroupBuyPatchReqDto request) {
        GroupBuyResDto response = groupBuyService.patchGroupBuy(groupBuyId, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "공동구매 삭제", description = "공동구매를 삭제합니다.")
    @DeleteMapping("/{groupBuyId}")
    public ResponseEntity<Void> deleteGroupBuy(
            @Parameter(description = "공동구매 ID") @PathVariable Long groupBuyId) {
        groupBuyService.deleteGroupBuy(groupBuyId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "공동구매 상태 조회", description = "공동구매의 현재 상태를 조회합니다.")
    @GetMapping("/{groupBuyId}/status")
    public ResponseEntity<GroupBuyStatusResDto> getGroupBuyStatus(
            @Parameter(description = "공동구매 ID") @PathVariable Long groupBuyId) {
        GroupBuyStatusResDto status = groupBuyService.getGroupBuyStatus(groupBuyId);
        return ResponseEntity.ok(status);
    }

    @Operation(summary = "회원별 참여 공동구매 조회", description = "회원이 참여한 공동구매 목록을 조회합니다.")
    @GetMapping("/members/{memberId}")
    public ResponseEntity<Page<GroupBuyResDto>> getGroupBuysByMemberId(
            @Parameter(description = "회원 ID") @PathVariable Long memberId,
            @PageableDefault(size = 5) Pageable pageable) {

        Page<GroupBuyResDto> responses = groupBuyService.getGroupBuysByMemberId(memberId, pageable);
        return ResponseEntity.ok(responses);
    }
}
