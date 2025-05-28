package com.team5.backend.domain.groupBuy.search.controller;

import com.team5.backend.domain.groupBuy.dto.GroupBuyResDto;
import com.team5.backend.domain.groupBuy.search.service.GroupBuyDbSearchService;
import com.team5.backend.global.dto.RsData;
import com.team5.backend.global.exception.RsDataUtil;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/groupBuy/db-search")
@RequiredArgsConstructor
public class GroupBuyDbSearchController {

    private final GroupBuyDbSearchService groupBuyDbSearchService;

    @Operation(summary = "DB 검색", description = "키워드(상품명)로 DB에서 직접 공동구매를 검색합니다.")
    @GetMapping
    public RsData<List<GroupBuyResDto>> search(@RequestParam String keyword) {
        List<GroupBuyResDto> result = groupBuyDbSearchService.search(keyword);

        if (result.isEmpty()) {
            return RsDataUtil.success("검색 결과가 없습니다.", result);
        }

        return RsDataUtil.success("검색 성공", result);
    }
}