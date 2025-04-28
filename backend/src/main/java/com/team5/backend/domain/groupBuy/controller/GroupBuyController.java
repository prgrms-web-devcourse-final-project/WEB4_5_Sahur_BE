package com.team5.backend.domain.groupBuy.controller;

import com.team5.backend.domain.groupBuy.dto.GroupBuyCreateReqDto;
import com.team5.backend.domain.groupBuy.dto.GroupBuyResDto;
import com.team5.backend.domain.groupBuy.dto.GroupBuyStatusResDto;
import com.team5.backend.domain.groupBuy.dto.GroupBuyUpdateReqDto;
import com.team5.backend.domain.groupBuy.entity.GroupBuy;
import com.team5.backend.domain.groupBuy.entity.GroupBuySortField;
import com.team5.backend.domain.groupBuy.service.GroupBuyService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/groupBuy")
@RequiredArgsConstructor
public class GroupBuyController {

    private final GroupBuyService groupBuyService;

    @PostMapping
    public ResponseEntity<GroupBuyResDto> createGroupBuy(@RequestBody GroupBuyCreateReqDto request) {
        GroupBuyResDto response = groupBuyService.createGroupBuy(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<GroupBuyResDto>> getAllGroupBuys(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "5") int size,
            @RequestParam(value = "sortField", defaultValue = "LATEST") GroupBuySortField sortField) {

        Pageable pageable = PageRequest.of(page, size);
        List<GroupBuyResDto> responses = groupBuyService.getAllGroupBuys(pageable, sortField);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/closing")
    public ResponseEntity<List<GroupBuyResDto>> getClosingGroupBuys(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "5") int size,
            @RequestParam(value = "sortField", defaultValue = "LATEST") GroupBuySortField sortField) {

        Pageable pageable = PageRequest.of(page, size);
        List<GroupBuyResDto> responses = groupBuyService.getTodayDeadlineGroupBuys(pageable, sortField);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{groupBuyId}")
    public ResponseEntity<GroupBuyResDto> getGroupBuyById(@PathVariable Long id) {
        return groupBuyService.getGroupBuyById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{groupBuyId}")
    public ResponseEntity<GroupBuyResDto> updateGroupBuy(@PathVariable Long id, @RequestBody GroupBuyUpdateReqDto request) {
        GroupBuyResDto response = groupBuyService.updateGroupBuy(id, request);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{groupBuyId}")
    public ResponseEntity<GroupBuyResDto> patchGroupBuy(@PathVariable Long id, @RequestBody GroupBuyUpdateReqDto request) {
        GroupBuyResDto response = groupBuyService.patchGroupBuy(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{groupBuyId}")
    public ResponseEntity<Void> deleteGroupBuy(@PathVariable Long id) {
        groupBuyService.deleteGroupBuy(id);
        return ResponseEntity.noContent().build();
    }
}
