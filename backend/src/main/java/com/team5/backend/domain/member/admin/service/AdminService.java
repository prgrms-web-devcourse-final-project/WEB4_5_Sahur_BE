package com.team5.backend.domain.member.admin.service;

import com.team5.backend.domain.groupBuy.dto.GroupBuyCreateReqDto;
import com.team5.backend.domain.groupBuy.entity.GroupBuy;
import com.team5.backend.domain.groupBuy.entity.GroupBuyStatus;
import com.team5.backend.domain.groupBuy.repository.GroupBuyRepository;
import com.team5.backend.domain.member.admin.dto.ProductRequestResDto;
import com.team5.backend.domain.member.admin.entity.ProductRequest;
import com.team5.backend.domain.member.admin.entity.ProductRequestStatus;
import com.team5.backend.domain.member.admin.repository.ProductRequestRepository;
import com.team5.backend.domain.product.dto.ProductResDto;
import com.team5.backend.domain.product.entity.Product;
import com.team5.backend.domain.product.repository.ProductRepository;
import com.team5.backend.global.exception.CustomException;
import com.team5.backend.global.exception.code.AdminErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final ProductRequestRepository productRequestRepository;
    private final ProductRepository productRepository;
    private final GroupBuyRepository groupBuyRepository;

    public Page<ProductRequestResDto> getProductRequests(Pageable pageable, ProductRequestStatus status) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

        Page<ProductRequest> pageResult = (status != null)
                ? productRequestRepository.findAllByStatus(status, sortedPageable)
                : productRequestRepository.findAll(sortedPageable);

        return pageResult.map(ProductRequestResDto::fromEntity);
    }

    @Transactional
    public void createGroupBuy(GroupBuyCreateReqDto request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new CustomException(AdminErrorCode.PRODUCT_NOT_FOUND));

        ProductRequest productRequest = productRequestRepository.findById(request.getProductRequestId())
                .orElseThrow(() -> new CustomException(AdminErrorCode.PRODUCT_REQUEST_NOT_FOUND));

        if (productRequest.getStatus() != ProductRequestStatus.APPROVED) {
            throw new CustomException(AdminErrorCode.INVALID_PRODUCT_REQUEST_STATUS);
        }

        GroupBuy groupBuy = GroupBuy.builder()
                .product(product)
                .targetParticipants(request.getTargetParticipants())
                .currentParticipantCount(0)
                .round(request.getRound())
                .deadline(request.getDeadline())
                .status(GroupBuyStatus.ONGOING)
                .createdAt(LocalDateTime.now())
                .build();

        groupBuyRepository.save(groupBuy);
    }

    @Transactional
    public void updateProductRequestStatus(Long productRequestId, ProductRequestStatus newStatus) {
        ProductRequest productRequest = productRequestRepository.findById(productRequestId)
                .orElseThrow(() -> new CustomException(AdminErrorCode.PRODUCT_REQUEST_NOT_FOUND));

        productRequest.changeStatus(newStatus);
    }

    @Transactional(readOnly = true)
    public Page<ProductResDto> getAllProducts(String search, Pageable pageable) {
        Page<Product> products;

        if (StringUtils.hasText(search)) {
            products = productRepository.findByTitleContainingIgnoreCase(search, pageable);
        } else {
            products = productRepository.findAll(pageable);
        }

        return products.map(ProductResDto::fromEntity);
    }

    @Transactional(readOnly = true)
    public ProductResDto getProductDetail(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new CustomException(AdminErrorCode.PRODUCT_NOT_FOUND));

        return ProductResDto.fromEntity(product);
    }


}
