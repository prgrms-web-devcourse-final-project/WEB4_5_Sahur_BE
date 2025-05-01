package com.team5.backend.domain.groupBuy.controller;

import com.team5.backend.domain.groupBuy.dto.*;
import com.team5.backend.domain.groupBuy.entity.GroupBuySortField;
import com.team5.backend.domain.groupBuy.service.GroupBuyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/groupBuy")
@RequiredArgsConstructor
public class GroupBuyController {

    private final GroupBuyService groupBuyService;

    @PostMapping
    public ResponseEntity<GroupBuyResDto> createGroupBuy(@RequestBody @Valid GroupBuyCreateReqDto request) {
        GroupBuyResDto response = groupBuyService.createGroupBuy(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<GroupBuyResDto>> getAllGroupBuys(
            @PageableDefault(size = 5) Pageable pageable,
            @RequestParam(value = "sortField", defaultValue = "LATEST") GroupBuySortField sortField) {

        Page<GroupBuyResDto> responses = groupBuyService.getAllGroupBuys(pageable, sortField);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/closing")
    public ResponseEntity<Page<GroupBuyResDto>> getClosingGroupBuys(
            @PageableDefault(size = 5) Pageable pageable,
            @RequestParam(value = "sortField", defaultValue = "LATEST") GroupBuySortField sortField) {

        Page<GroupBuyResDto> responses = groupBuyService.getTodayDeadlineGroupBuys(pageable, sortField);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{groupBuyId}")
    public ResponseEntity<GroupBuyResDto> getGroupBuyById(@PathVariable Long groupBuyId) {
        return groupBuyService.getGroupBuyById(groupBuyId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{groupBuyId}")
    public ResponseEntity<GroupBuyResDto> updateGroupBuy(@PathVariable Long groupBuyId, @Valid @RequestBody GroupBuyUpdateReqDto request) {
        GroupBuyResDto response = groupBuyService.updateGroupBuy(groupBuyId, request);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{groupBuyId}")
    public ResponseEntity<GroupBuyResDto> patchGroupBuy(@PathVariable Long groupBuyId, @RequestBody GroupBuyPatchReqDto request) {
        GroupBuyResDto response = groupBuyService.patchGroupBuy(groupBuyId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{groupBuyId}")
    public ResponseEntity<Void> deleteGroupBuy(@PathVariable Long groupBuyId) {
        groupBuyService.deleteGroupBuy(groupBuyId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{groupBuyId}/status")
    public ResponseEntity<GroupBuyStatusResDto> getGroupBuyStatus(@PathVariable Long groupBuyId) {
        GroupBuyStatusResDto status = groupBuyService.getGroupBuyStatus(groupBuyId);
        return ResponseEntity.ok(status);
    }

//    @GetMapping("/members/{memberId}")
//    public ResponseEntity<Page<GroupBuyResDto>> getGroupBuysByMemberId(
//            @PathVariable Long memberId,
//            @PageableDefault(size = 5) Pageable pageable,
//            @RequestParam(value = "sortField", defaultValue = "LATEST") GroupBuySortField sortField) {
//
//        Page<GroupBuyResDto> responses = groupBuyService.getGroupBuysByMemberId(memberId, pageable, sortField);
//        return ResponseEntity.ok(responses);
//    }
}
