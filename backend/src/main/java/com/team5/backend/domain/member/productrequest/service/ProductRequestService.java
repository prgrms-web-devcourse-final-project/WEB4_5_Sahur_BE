package com.team5.backend.domain.member.productrequest.service;

import com.team5.backend.domain.category.entity.Category;
import com.team5.backend.domain.category.repository.CategoryRepository;
import com.team5.backend.domain.member.productrequest.dto.ProductRequestCreateReqDto;
import com.team5.backend.domain.member.productrequest.dto.ProductRequestResDto;
import com.team5.backend.domain.member.productrequest.dto.ProductRequestUpdateReqDto;
import com.team5.backend.domain.member.productrequest.entity.ProductRequest;
import com.team5.backend.domain.member.productrequest.entity.ProductRequestStatus;
import com.team5.backend.domain.member.productrequest.repository.ProductRequestRepository;
import com.team5.backend.domain.member.member.entity.Member;
import com.team5.backend.domain.member.member.repository.MemberRepository;
import com.team5.backend.global.exception.CustomException;
import com.team5.backend.global.exception.code.ProductRequestErrorCode;
import com.team5.backend.global.security.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductRequestService {

    private final ProductRequestRepository productRequestRepository;
    private final CategoryRepository categoryRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public ProductRequestResDto createRequest(ProductRequestCreateReqDto dto, PrincipalDetails userDetails) {
        Member member = getMember(userDetails);
        Category category = getCategory(dto.getCategoryId());

        ProductRequest request = ProductRequest.builder()
                .member(member)
                .category(category)
                .title(dto.getTitle())
                .productUrl(dto.getProductUrl())
                .imageUrls(dto.getImageUrls())
                .description(dto.getDescription())
                .status(ProductRequestStatus.WAITING)
                .build();

        return ProductRequestResDto.fromEntity(productRequestRepository.save(request));
    }

    @Transactional(readOnly = true)
    public Page<ProductRequestResDto> getAllRequests(Pageable pageable) {
        Pageable sorted = forceCreatedAtDesc(pageable);
        return productRequestRepository.findAll(sorted)
                .map(ProductRequestResDto::fromEntity);
    }

    @Transactional(readOnly = true)
    public Page<ProductRequestResDto> getAllRequestsByStatus(ProductRequestStatus status, Pageable pageable) {
        Pageable sorted = forceCreatedAtDesc(pageable);
        return productRequestRepository.findAllByStatus(status, sorted)
                .map(ProductRequestResDto::fromEntity);
    }


    @Transactional(readOnly = true)
    public Page<ProductRequestResDto> getRequestsByMember(PrincipalDetails userDetails, Pageable pageable) {
        Long memberId = userDetails.getMember().getMemberId();
        Pageable sorted = forceCreatedAtDesc(pageable);
        return productRequestRepository.findByMemberMemberId(memberId, sorted)
                .map(ProductRequestResDto::fromEntity);
    }

    @Transactional(readOnly = true)
    public ProductRequestResDto getRequest(Long productRequestId) {
        ProductRequest request = getRequestOrThrow(productRequestId);
        return ProductRequestResDto.fromEntity(request);
    }

    @Transactional
    public ProductRequestResDto updateRequest(Long productRequestId, ProductRequestUpdateReqDto dto, PrincipalDetails userDetails) {
        ProductRequest request = getRequestOrThrow(productRequestId);
        Member member = getMember(userDetails);

        if (!request.getMember().equals(member)) {
            throw new CustomException(ProductRequestErrorCode.PRODUCT_REQUEST_ACCESS_DENIED);
        }

        if (dto.getCategoryId() != null) {
            Category category = getCategory(dto.getCategoryId());
            request.setCategory(category);
        }

        if (dto.getTitle() != null) {
            request.setTitle(dto.getTitle());
        }

        if (dto.getProductUrl() != null) {
            request.setProductUrl(dto.getProductUrl());
        }

        if (dto.getImageUrls() != null) {
            request.setImageUrls(dto.getImageUrls());
        }

        if (dto.getDescription() != null) {
            request.setDescription(dto.getDescription());
        }

        return ProductRequestResDto.fromEntity(productRequestRepository.save(request));
    }

    @Transactional
    public ProductRequestResDto updateStatus(Long productRequestId, String confirm) {
        ProductRequest request = getRequestOrThrow(productRequestId);

        ProductRequestStatus newStatus = switch (confirm.toLowerCase()) {
            case "approve" -> ProductRequestStatus.APPROVED;
            case "reject" -> ProductRequestStatus.REJECTED;
            default -> throw new CustomException(ProductRequestErrorCode.INVALID_PRODUCT_REQUEST_STATUS);
        };

        request.changeStatus(newStatus);
        return ProductRequestResDto.fromEntity(request);
    }

    @Transactional
    public void deleteRequest(Long productRequestId, PrincipalDetails userDetails) {
        ProductRequest request = getRequestOrThrow(productRequestId);
        Member member = getMember(userDetails);

        if (!request.getMember().equals(member)) {
            throw new CustomException(ProductRequestErrorCode.PRODUCT_REQUEST_ACCESS_DENIED);
        }

        productRequestRepository.delete(request);
    }

    // ✅ 정렬 강제 고정 메서드
    private Pageable forceCreatedAtDesc(Pageable original) {
        return PageRequest.of(
                original.getPageNumber(),
                original.getPageSize(),
                Sort.by(Sort.Direction.DESC, "createdAt")
        );
    }

    private ProductRequest getRequestOrThrow(Long id) {
        return productRequestRepository.findById(id)
                .orElseThrow(() -> new CustomException(ProductRequestErrorCode.PRODUCT_REQUEST_NOT_FOUND));
    }

    private Member getMember(PrincipalDetails userDetails) {
        return memberRepository.findById(userDetails.getMember().getMemberId())
                .orElseThrow(() -> new CustomException(ProductRequestErrorCode.PRODUCT_REQUEST_ACCESS_DENIED));
    }

    private Category getCategory(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new CustomException(ProductRequestErrorCode.CATEGORY_NOT_FOUND));
    }
}
