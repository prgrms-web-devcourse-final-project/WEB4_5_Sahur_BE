package com.team5.backend.domain.groupBuy.controller;

import com.team5.backend.domain.groupBuy.dto.GroupBuyCreateReqDto;
import com.team5.backend.domain.groupBuy.dto.GroupBuyResDto;
import com.team5.backend.domain.groupBuy.dto.GroupBuyUpdateReqDto;
import com.team5.backend.domain.groupBuy.entity.GroupBuy;
import com.team5.backend.domain.groupBuy.service.GroupBuyService;
import lombok.RequiredArgsConstructor;
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
    public ResponseEntity<List<GroupBuyResDto>> getAllGroupBuys() {
        List<GroupBuyResDto> responses = groupBuyService.getAllGroupBuys();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GroupBuyResDto> getGroupBuyById(@PathVariable Long id) {
        return groupBuyService.getGroupBuyById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<GroupBuyResDto> updateGroupBuy(@PathVariable Long id, @RequestBody GroupBuyUpdateReqDto request) {
        GroupBuyResDto response = groupBuyService.updateGroupBuy(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGroupBuy(@PathVariable Long id) {
        groupBuyService.deleteGroupBuy(id);
        return ResponseEntity.noContent().build();
    }
}
