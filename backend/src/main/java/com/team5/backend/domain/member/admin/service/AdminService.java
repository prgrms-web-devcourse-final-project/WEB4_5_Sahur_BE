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

    // ✅ ProductRequest 조회 (Page 반환)
    public Page<ProductRequestResDto> getProductRequests(Pageable pageable, ProductRequestStatus status) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

        Page<ProductRequest> pageResult;

        if (status != null) {
            pageResult = productRequestRepository.findAllByStatus(status, sortedPageable);
        } else {
            pageResult = productRequestRepository.findAll(sortedPageable);
        }

        List<ProductRequestResDto> content = pageResult.stream()
                .map(this::toProductRequestResponse)
                .collect(Collectors.toList());

        return new PageImpl<>(content, sortedPageable, pageResult.getTotalElements());
    }

    // ✅ GroupBuyRequest 조회 (Page 반환)
    public Page<GroupBuyRequestResDto> getAllGroupBuyRequests(Pageable pageable) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

        Page<GroupBuyRequest> pageResult = groupBuyRequestRepository.findAll(sortedPageable);

        List<GroupBuyRequestResDto> content = pageResult.stream()
                .map(this::toGroupBuyRequestResponse)
                .collect(Collectors.toList());

        return new PageImpl<>(content, sortedPageable, pageResult.getTotalElements());
    }

    private ProductRequestResDto toProductRequestResponse(ProductRequest productRequest) {
        return ProductRequestResDto.builder()
                .productRequestId(productRequest.getProductRequestId())
                .memberId(productRequest.getMember().getMemberId())
                .categoryId(productRequest.getCategory().getCategoryId())
                .title(productRequest.getTitle())
                .productUrl(productRequest.getProductUrl())
                .etc(productRequest.getEtc())
                .status(productRequest.getStatus())
                .build();
    }

    private GroupBuyRequestResDto toGroupBuyRequestResponse(GroupBuyRequest groupBuyRequest) {
        return GroupBuyRequestResDto.builder()
                .groupBuyRequestId(groupBuyRequest.getGroupBuyRequestId())
                .productId(groupBuyRequest.getProduct().getProductId())
                .memberId(groupBuyRequest.getMember().getMemberId())
                .build();
    }
}
