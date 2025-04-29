package com.team5.backend.domain.member.admin.controller;

import com.team5.backend.domain.member.admin.dto.GroupBuyRequestResDto;
import com.team5.backend.domain.member.admin.dto.ProductRequestResDto;
import com.team5.backend.domain.member.admin.entity.ProductRequestStatus;
import com.team5.backend.domain.member.admin.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;


    @GetMapping("/productRequest/list")
    public List<ProductRequestResDto> getProductRequests(
            Pageable pageable,
            @RequestParam(value = "status", required = false) ProductRequestStatus status
    ) {
        return adminService.getProductRequests(pageable, status);
    }

    @GetMapping("/groupBuyRequest/list")
    public List<GroupBuyRequestResDto> getGroupBuyRequests(Pageable pageable) {
        return adminService.getAllGroupBuyRequests(pageable);
    }
}
