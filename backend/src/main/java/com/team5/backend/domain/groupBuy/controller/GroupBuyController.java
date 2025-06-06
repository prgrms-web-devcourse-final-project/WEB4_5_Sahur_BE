package com.team5.backend.domain.groupBuy.controller;

import com.team5.backend.domain.category.entity.CategoryType;
import com.team5.backend.domain.groupBuy.dto.*;
import com.team5.backend.domain.groupBuy.entity.GroupBuySortField;
import com.team5.backend.domain.groupBuy.service.GroupBuyService;
import com.team5.backend.global.annotation.CheckAdmin;
import com.team5.backend.global.dto.Empty;
import com.team5.backend.global.dto.RsData;
import com.team5.backend.global.exception.RsDataUtil;
import com.team5.backend.global.security.PrincipalDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "GroupBuy", description = "공동구매 관련 API")
@RestController
@RequestMapping("/api/v1/groupBuy")
@RequiredArgsConstructor
public class GroupBuyController {

    private final GroupBuyService groupBuyService;

    @Operation(summary = "공동구매 생성", description = "신규 공동구매를 생성합니다.")
    @CheckAdmin
    @PostMapping
    public RsData<GroupBuyResDto> createGroupBuy(@RequestBody @Valid GroupBuyCreateReqDto request) {
        GroupBuyResDto response = groupBuyService.createGroupBuy(request);
        return RsDataUtil.success("공동구매 생성 성공", response);
    }

    @Operation(summary = "전체 공동구매 조회 (전체)", description = "모든 상태의 공동구매 목록을 조회합니다.")
    @CheckAdmin
    @GetMapping("/list")
    public RsData<Page<GroupBuyResDto>> getAllGroupBuysForAdmin(
            @Parameter(description = "페이지 정보") @PageableDefault(size = 5) Pageable pageable,
            @Parameter(description = "정렬 기준", example = "LATEST")
            @RequestParam(value = "sortField", defaultValue = "LATEST") GroupBuySortField sortField) {

        Page<GroupBuyResDto> responses = groupBuyService.getAllGroupBuys(pageable, sortField);
        return RsDataUtil.success("공동구매 전체 조회 성공", responses);
    }

    @Operation(summary = "진행 중 공동구매 조회 (진행중인 것만)", description = "진행 중인 공동구매만 조회합니다.")
    @GetMapping("/list/onGoing")
    public RsData<Page<GroupBuyResDto>> getAllOngoingGroupBuys(
            @Parameter(description = "페이지 정보") @PageableDefault(size = 8) Pageable pageable,
            @Parameter(description = "정렬 기준", example = "LATEST")
            @RequestParam(value = "sortField", defaultValue = "LATEST") GroupBuySortField sortField) {

        Page<GroupBuyResDto> responses = groupBuyService.getAllONGINGGroupBuys(pageable, sortField);
        return RsDataUtil.success("진행 중 공동구매 조회 성공", responses);
    }

    @Operation(summary = "오늘 마감 공동구매 조회", description = "오늘 마감 예정인 공동구매 목록을 조회합니다.")
    @GetMapping("/closing")
    public RsData<Page<GroupBuyResDto>> getClosingGroupBuys(
            @Parameter(description = "페이지 정보") @PageableDefault(size = 5) Pageable pageable,
            @Parameter(description = "정렬 기준", example = "LATEST")
            @RequestParam(value = "sortField", defaultValue = "LATEST") GroupBuySortField sortField) {

        Page<GroupBuyResDto> responses = groupBuyService.getTodayDeadlineGroupBuys(pageable, sortField);
        return RsDataUtil.success("오늘 마감 공동구매 조회 성공", responses);
    }

    @Operation(summary = "공동구매 단건 조회", description = "특정 공동구매의 상세 정보를 조회합니다.")
    @GetMapping("/{groupBuyId}")
    public RsData<GroupBuyDetailResDto> getGroupBuyById(
            @Parameter(description = "공동구매 ID") @PathVariable Long groupBuyId,
            @AuthenticationPrincipal PrincipalDetails userDetails
    ) {
        GroupBuyDetailResDto data = groupBuyService.getGroupBuyById(groupBuyId, userDetails);
        return RsDataUtil.success("공동구매 단건 조회 성공", data);
    }

    @Operation(summary = "공동구매 수정", description = "전체 필드를 수정합니다.")
    @CheckAdmin
    @PutMapping("/{groupBuyId}")
    public RsData<GroupBuyResDto> updateGroupBuy(
            @Parameter(description = "공동구매 ID") @PathVariable Long groupBuyId,
            @Valid @RequestBody GroupBuyUpdateReqDto request) {
        GroupBuyResDto response = groupBuyService.updateGroupBuy(groupBuyId, request);
        return RsDataUtil.success("공동구매 수정 성공", response);
    }

    @Operation(summary = "공동구매 부분 수정", description = "특정 필드만 부분적으로 수정합니다.")
    @CheckAdmin
    @PatchMapping("/{groupBuyId}")
    public RsData<GroupBuyResDto> patchGroupBuy(
            @Parameter(description = "공동구매 ID") @PathVariable Long groupBuyId,
            @RequestBody GroupBuyPatchReqDto request) {
        GroupBuyResDto response = groupBuyService.patchGroupBuy(groupBuyId, request);
        return RsDataUtil.success("공동구매 일부 수정 성공", response);
    }

    @Operation(summary = "공동구매 삭제", description = "공동구매를 삭제합니다.")
    @CheckAdmin
    @DeleteMapping("/{groupBuyId}")
    public RsData<Empty> deleteGroupBuy(
            @Parameter(description = "공동구매 ID") @PathVariable Long groupBuyId) {
        groupBuyService.deleteGroupBuy(groupBuyId);
        return RsDataUtil.success("공동구매 삭제 성공");
    }

    @Operation(summary = "공동구매 상태 조회", description = "공동구매의 현재 상태를 조회합니다.")
    @GetMapping("/{groupBuyId}/status")
    public RsData<GroupBuyStatusResDto> getGroupBuyStatus(
            @Parameter(description = "공동구매 ID") @PathVariable Long groupBuyId) {
        GroupBuyStatusResDto status = groupBuyService.getGroupBuyStatus(groupBuyId);
        return RsDataUtil.success("공동구매 상태 조회 성공", status);
    }

    @Operation(summary = "회원별 참여 공동구매 조회", description = "회원이 참여한 공동구매 목록을 조회합니다.")
    @GetMapping("/member")
    public RsData<Page<GroupBuyResDto>> getGroupBuysByMember(
            @AuthenticationPrincipal PrincipalDetails userDetails,
            @PageableDefault(size = 5) Pageable pageable) {

        Page<GroupBuyResDto> responses = groupBuyService.getGroupBuysByMember(userDetails, pageable);
        return RsDataUtil.success("회원 참여 공동구매 조회 성공", responses);
    }

    @Operation(summary = "공동구매 마감 처리", description = "공동구매를 CLOSED 상태로 마감합니다.")
    @CheckAdmin
    @PatchMapping("/{groupBuyId}/close")
    public RsData<Empty> closeGroupBuy(
            @Parameter(description = "공동구매 ID") @PathVariable Long groupBuyId) {
        groupBuyService.closeGroupBuy(groupBuyId);
        return RsDataUtil.success("공동구매 상태 마감 성공");
    }
    @Operation(summary = "인기순 Top 3 공동구매 조회", description = "Dib(관심상품) 수 기준 상위 3개의 공동구매를 조회합니다.")
    @GetMapping("/popular")
    public RsData<List<GroupBuyResDto>> getTop3GroupBuysByDibs() {
        List<GroupBuyResDto> responses = groupBuyService.getTop3GroupBuysByDibs();
        return RsDataUtil.success("Top 3 공동구매 조회 성공", responses);
    }

    @Operation(summary = "같은 카테고리의 랜덤 공동구매 3개 조회", description = "현재 공동구매와 같은 카테고리의 진행 중인 공동구매를 무작위로 3개 반환합니다.")
    @GetMapping("/{groupBuyId}/related")
    public RsData<List<GroupBuyResDto>> getRandomTop3GroupBuysBySameCategory(
            @Parameter(description = "공동구매 ID") @PathVariable Long groupBuyId) {

        List<GroupBuyResDto> relatedGroupBuys = groupBuyService.getRandomTop3GroupBuysBySameCategory(groupBuyId);
        return RsDataUtil.success("같은 카테고리의 공동구매 3개 조회 성공", relatedGroupBuys);
    }

    @Operation(
            summary = "카테고리 타입별 진행 중 공동구매 조회",
            description = "카테고리 타입(CategoryType)을 기준으로 진행 중인 공동구매 목록을 정렬 기준과 함께 조회합니다."
    )
    @GetMapping("/category-type/{categoryType}/onGoing")
    public RsData<Page<GroupBuyResDto>> getOngoingGroupBuysByCategoryType(
            @Parameter(description = "카테고리 타입", example = "ALL")
            @PathVariable CategoryType categoryType,

            // ✅ Swagger UI에서 pageable 필드를 숨김 처리 (Swagger에서 엉뚱한 JSON 입력 못하게 함)
            @Parameter(hidden = true)
            @PageableDefault(size = 8) Pageable pageable,

            @Parameter(description = "정렬 기준", example = "LATEST")
            @RequestParam(defaultValue = "LATEST") GroupBuySortField sortField
    ) {
        Page<GroupBuyResDto> responses = groupBuyService.getOngoingGroupBuysByCategoryType(categoryType, pageable, sortField);

        if (responses.isEmpty()) {
            return RsDataUtil.success("해당 카테고리 타입에 진행 중인 공동구매가 없습니다.", responses);
        }
        return RsDataUtil.success("카테고리 타입별 진행 중 공동구매 조회 성공", responses);
    }

}
