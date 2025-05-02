package com.team5.backend.domain.history.controller;

import com.team5.backend.domain.history.dto.HistoryCreateReqDto;
import com.team5.backend.domain.history.dto.HistoryResDto;
import com.team5.backend.domain.history.dto.HistoryUpdateReqDto;
import com.team5.backend.domain.history.service.HistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "History", description = "구매 이력 관련 API")
@RestController
@RequestMapping("/api/v1/histories")
@RequiredArgsConstructor
public class HistoryController {

    private final HistoryService historyService;

    @Operation(summary = "구매 이력 생성", description = "구매 이력을 생성합니다.")
    @PostMapping
    public ResponseEntity<HistoryResDto> createHistory(
            @Parameter(description = "Access Token (Bearer 포함)", required = true)
            @RequestHeader(value = "Authorization") String token,
            @RequestBody HistoryCreateReqDto request) {
        HistoryResDto response = historyService.createHistory(request, token);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "전체 구매 이력 조회", description = "모든 구매 이력을 조회합니다 (최신순 정렬).")
    @GetMapping
    public ResponseEntity<Page<HistoryResDto>> getAllHistories(
            @Parameter(description = "페이지 정보") @PageableDefault(size = 5) Pageable pageable) {

        Page<HistoryResDto> responses = historyService.getAllHistories(pageable);
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "구매 이력 단건 조회", description = "ID로 특정 구매 이력을 조회합니다.")
    @GetMapping("/{id}")
    public ResponseEntity<HistoryResDto> getHistoryById(
            @Parameter(description = "구매 이력 ID") @PathVariable Long id) {
        return historyService.getHistoryById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "구매 이력 수정", description = "구매 이력의 writable 값을 수정합니다.")
    @PutMapping("/{id}")
    public ResponseEntity<HistoryResDto> updateHistory(
            @Parameter(description = "구매 이력 ID") @PathVariable Long id,
            @RequestBody HistoryUpdateReqDto request) {
        HistoryResDto response = historyService.updateHistory(id, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "구매 이력 삭제", description = "구매 이력을 삭제합니다.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHistory(
            @Parameter(description = "구매 이력 ID") @PathVariable Long id) {
        historyService.deleteHistory(id);
        return ResponseEntity.noContent().build();
    }
}
