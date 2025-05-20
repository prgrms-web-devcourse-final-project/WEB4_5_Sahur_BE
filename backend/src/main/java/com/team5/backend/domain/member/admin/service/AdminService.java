package com.team5.backend.domain.member.admin.service;

import com.team5.backend.domain.member.productrequest.dto.ProductRequestResDto;
import com.team5.backend.domain.member.productrequest.entity.ProductRequest;
import com.team5.backend.domain.member.productrequest.entity.ProductRequestStatus;
import com.team5.backend.domain.member.productrequest.repository.ProductRequestRepository;
import com.team5.backend.global.exception.CustomException;
import com.team5.backend.global.exception.code.AdminErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final ProductRequestRepository productRequestRepository;

    public Page<ProductRequestResDto> getProductRequests(Pageable pageable, ProductRequestStatus status) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

        Page<ProductRequest> pageResult = (status != null)
                ? productRequestRepository.findAllByStatus(status, sortedPageable)
                : productRequestRepository.findAll(sortedPageable);

        return pageResult.map(ProductRequestResDto::fromEntity);
    }

    @Transactional
    public void updateProductRequestStatus(Long productRequestId, ProductRequestStatus newStatus) {
        ProductRequest productRequest = productRequestRepository.findById(productRequestId)
                .orElseThrow(() -> new CustomException(AdminErrorCode.PRODUCT_REQUEST_NOT_FOUND));

        productRequest.changeStatus(newStatus);
    }

}
