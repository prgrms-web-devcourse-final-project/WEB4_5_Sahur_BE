package com.team5.backend.domain.dibs.controller;

import com.team5.backend.domain.dibs.dto.DibsCreateReqDto;
import com.team5.backend.domain.dibs.dto.DibsResDto;
import com.team5.backend.domain.dibs.service.DibsService;
import com.team5.backend.global.dto.RsData;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/dibs")
@RequiredArgsConstructor
public class DibsController {

    private final DibsService dibsService;

    @PostMapping("/products/{productId}/dibs")
    public RsData<DibsResDto> createDibs(
            @PathVariable Long productId,
            @RequestParam Long memberId){
        DibsCreateReqDto request = new DibsCreateReqDto(memberId, productId);
        DibsResDto response = dibsService.createDibs(request);
        return new RsData<>("200", "관심상품 등록 완료", response);
    }

//    @GetMapping("/members/{memberId}/dibs")
//    public ResponseEntity<List<DibsResDto>> getAllDibs() {
//        List<DibsResDto> responses = dibsService.getAllDibs();
//        return ResponseEntity.ok(responses);
//    }

    @DeleteMapping("/products/{productId}/dibs")
    public RsData<Void> deleteDibs(
            @PathVariable Long productId,
            @RequestParam Long memberId){
        dibsService.deleteByProductAndMember(productId, memberId);
        return new RsData<>("200", "관심상품 삭제 완료", null);
    }

    @GetMapping("/members/{memberId}/dibs")
    public RsData<?> getDibsByMember(
            @PathVariable Long memberId,
            @PageableDefault(size = 6) Pageable pageable,
            @RequestParam(required = false) Boolean paged) {

        if (Boolean.TRUE.equals(paged)) {
            Page<DibsResDto> pagedDibs = dibsService.getPagedDibsByMemberId(memberId, pageable);
            return new RsData<>("200", "관심상품 페이징 조회 완료", pagedDibs);
        }

        List<DibsResDto> all = dibsService.getAllDibsByMemberId(memberId);
        return new RsData<>("200", "관심상품 전체 조회 완료", all);
    }
}
