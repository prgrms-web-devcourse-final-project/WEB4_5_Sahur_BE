package com.team5.backend.domain.member.admin.service;

import com.team5.backend.domain.member.admin.dto.GroupBuyRequestResDto;
import com.team5.backend.domain.member.admin.dto.ProductRequestResDto;
import com.team5.backend.domain.member.admin.entity.GroupBuyRequest;
import com.team5.backend.domain.member.admin.entity.ProductRequest;
import com.team5.backend.domain.member.admin.entity.ProductRequestStatus;
import com.team5.backend.domain.member.admin.repository.GroupBuyRequestRepository;
import com.team5.backend.domain.member.admin.repository.ProductRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final ProductRequestRepository productRequestRepository;
    private final GroupBuyRequestRepository groupBuyRequestRepository;

    public Page<ProductRequestResDto> getProductRequests(Pageable pageable, ProductRequestStatus status) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

        Page<ProductRequest> pageResult = (status != null)
                ? productRequestRepository.findAllByStatus(status, sortedPageable)
                : productRequestRepository.findAll(sortedPageable);

        List<ProductRequestResDto> content = pageResult.stream()
                .map(ProductRequestResDto::fromEntity)
                .collect(Collectors.toList());

        return new PageImpl<>(content, sortedPageable, pageResult.getTotalElements());
    }

    public Page<GroupBuyRequestResDto> getAllGroupBuyRequests(Pageable pageable) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

        Page<GroupBuyRequest> pageResult = groupBuyRequestRepository.findAll(sortedPageable);

        List<GroupBuyRequestResDto> content = pageResult.stream()
                .map(GroupBuyRequestResDto::fromEntity)
                .collect(Collectors.toList());

        return new PageImpl<>(content, sortedPageable, pageResult.getTotalElements());
    }
}
