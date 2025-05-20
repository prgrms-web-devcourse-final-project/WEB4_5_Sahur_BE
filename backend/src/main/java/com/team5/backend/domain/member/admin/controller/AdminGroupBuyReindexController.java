package com.team5.backend.domain.member.admin.controller;

import com.team5.backend.domain.groupBuy.search.service.GroupBuyReindexService;
import com.team5.backend.global.dto.RsData;
import com.team5.backend.global.exception.RsDataUtil;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/groupBuy/reindex")
@RequiredArgsConstructor
@Slf4j
public class AdminGroupBuyReindexController {

    private final GroupBuyReindexService groupBuyReindexService;

    @Operation(summary = "[관리자] 공동구매 재색인", description = "모든 공동구매 데이터를 Elasticsearch에 다시 색인합니다.")
    @PostMapping
    public RsData<Void> reindexAll() {
        log.info("🔁 [관리자 재색인 요청] /api/v1/admin/group-buys/reindex 호출됨");
        groupBuyReindexService.reindexAll();
        log.info("✅ [관리자 재색인 완료]");
        return RsDataUtil.success("공동구매 재색인 완료", null);
    }
}
