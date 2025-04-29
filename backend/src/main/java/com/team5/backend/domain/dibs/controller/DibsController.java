package com.team5.backend.domain.dibs.controller;

import com.team5.backend.domain.dibs.dto.DibsCreateReqDto;
import com.team5.backend.domain.dibs.dto.DibsResDto;
import com.team5.backend.domain.dibs.service.DibsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/dibs")
@RequiredArgsConstructor
public class DibsController {

    private final DibsService dibsService;

    @PostMapping
    public ResponseEntity<DibsResDto> createDibs(@RequestBody @Valid DibsCreateReqDto request) {
        DibsResDto response = dibsService.createDibs(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<DibsResDto>> getAllDibs() {
        List<DibsResDto> responses = dibsService.getAllDibs();
        return ResponseEntity.ok(responses);
    }

    @DeleteMapping("/{dibsId}")
    public ResponseEntity<Void> deleteDibs(@PathVariable Long dibsId) {
        dibsService.deleteDibs(dibsId);
        return ResponseEntity.noContent().build();
    }
}
