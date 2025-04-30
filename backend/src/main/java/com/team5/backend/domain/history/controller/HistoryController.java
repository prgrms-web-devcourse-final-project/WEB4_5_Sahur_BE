package com.team5.backend.domain.history.controller;

import com.team5.backend.domain.history.dto.HistoryCreateReqDto;
import com.team5.backend.domain.history.dto.HistoryResDto;
import com.team5.backend.domain.history.dto.HistoryUpdateReqDto;
import com.team5.backend.domain.history.service.HistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/histories")
@RequiredArgsConstructor
public class HistoryController {

    private final HistoryService historyService;

    @PostMapping
    public ResponseEntity<HistoryResDto> createHistory(@RequestBody HistoryCreateReqDto request) {
        HistoryResDto response = historyService.createHistory(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<HistoryResDto>> getAllHistories(
            @PageableDefault(size = 5) Pageable pageable) {

        Page<HistoryResDto> responses = historyService.getAllHistories(pageable);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<HistoryResDto> getHistoryById(@PathVariable Long id) {
        return historyService.getHistoryById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<HistoryResDto> updateHistory(@PathVariable Long id, @RequestBody HistoryUpdateReqDto request) {
        HistoryResDto response = historyService.updateHistory(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHistory(@PathVariable Long id) {
        historyService.deleteHistory(id);
        return ResponseEntity.noContent().build();
    }
}
