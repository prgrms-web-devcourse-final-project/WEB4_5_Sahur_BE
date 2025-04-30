package com.team5.backend.domain.member.admin.controller;

import com.team5.backend.domain.member.admin.dto.GroupBuyRequestResDto;
import com.team5.backend.domain.member.admin.dto.ProductRequestResDto;
import com.team5.backend.domain.member.admin.entity.ProductRequestStatus;
import com.team5.backend.domain.member.admin.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/productRequest/list")
    public ResponseEntity<Page<ProductRequestResDto>> getProductRequests(
            Pageable pageable,
            @RequestParam(value = "status", required = false) ProductRequestStatus status
    ) {
        Page<ProductRequestResDto> result = adminService.getProductRequests(pageable, status);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/groupBuyRequest/list")
    public ResponseEntity<Page<GroupBuyRequestResDto>> getGroupBuyRequests(Pageable pageable) {
        Page<GroupBuyRequestResDto> result = adminService.getAllGroupBuyRequests(pageable);
        return ResponseEntity.ok(result);
    }
}
