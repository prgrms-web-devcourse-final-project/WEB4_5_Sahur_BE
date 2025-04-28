package com.team5.backend.domain.history.controller;

import com.team5.backend.domain.history.dto.HistoryCreateReqDto;
import com.team5.backend.domain.history.dto.HistoryResDto;
import com.team5.backend.domain.history.dto.HistoryUpdateReqDto;
import com.team5.backend.domain.history.service.HistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/histories")
@RequiredArgsConstructor
public class HistoryController {

    private final HistoryService historyService;

    @PostMapping
    public HistoryResDto createHistory(@RequestBody HistoryCreateReqDto request) {
        return historyService.createHistory(request);
    }

    @GetMapping
    public List<HistoryResDto> getAllHistories() {
        return historyService.getAllHistories();
    }

    @GetMapping("/{id}")
    public HistoryResDto getHistoryById(@PathVariable Long id) {
        return historyService.getHistoryById(id)
                .orElseThrow(() -> new RuntimeException("History not found with id " + id));
    }

    @PutMapping("/{id}")
    public HistoryResDto updateHistory(@PathVariable Long id, @RequestBody HistoryUpdateReqDto request) {
        return historyService.updateHistory(id, request);
    }

    @DeleteMapping("/{id}")
    public void deleteHistory(@PathVariable Long id) {
        historyService.deleteHistory(id);
    }
}
