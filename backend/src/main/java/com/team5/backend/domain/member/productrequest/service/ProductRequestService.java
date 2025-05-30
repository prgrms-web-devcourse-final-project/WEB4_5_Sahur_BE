package com.team5.backend.domain.member.productrequest.service;

import java.io.IOException;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.team5.backend.domain.category.entity.Category;
import com.team5.backend.domain.category.repository.CategoryRepository;
import com.team5.backend.domain.member.member.entity.Member;
import com.team5.backend.domain.member.member.repository.MemberRepository;
import com.team5.backend.domain.member.productrequest.dto.ProductRequestCreateReqDto;
import com.team5.backend.domain.member.productrequest.dto.ProductRequestDetailResDto;
import com.team5.backend.domain.member.productrequest.dto.ProductRequestListResDto;
import com.team5.backend.domain.member.productrequest.dto.ProductRequestUpdateReqDto;
import com.team5.backend.domain.member.productrequest.entity.ProductRequest;
import com.team5.backend.domain.member.productrequest.entity.ProductRequestStatus;
import com.team5.backend.domain.member.productrequest.repository.ProductRequestRepository;
import com.team5.backend.domain.notification.redis.NotificationPublisher;
import com.team5.backend.domain.notification.template.NotificationTemplateType;
import com.team5.backend.domain.product.entity.Product;
import com.team5.backend.domain.product.repository.ProductRepository;
import com.team5.backend.global.exception.CustomException;
import com.team5.backend.global.exception.code.ProductRequestErrorCode;
import com.team5.backend.global.security.PrincipalDetails;
import com.team5.backend.global.util.ImageType;
import com.team5.backend.global.util.ImageUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductRequestService {

    private final ProductRequestRepository productRequestRepository;
    private final CategoryRepository categoryRepository;
    private final MemberRepository memberRepository;
    private final ImageUtil imageUtil;
    private final ProductRepository productRepository;
    private final NotificationPublisher notificationPublisher;

    @Transactional
    public ProductRequestDetailResDto createRequest(ProductRequestCreateReqDto dto, List<MultipartFile> imageFiles, PrincipalDetails userDetails) throws IOException {
        Member member = getMember(userDetails);

        if (dto == null || imageFiles == null) {
            throw new CustomException(ProductRequestErrorCode.INVALID_PRODUCT_REQUEST_STATUS);
        }

        Category category = getCategory(dto.getCategoryId());

        List<String> imageUrls = imageUtil.saveImages(imageFiles, ImageType.PRODUCT);

        ProductRequest request = ProductRequest.builder()
                .member(member)
                .category(category)
                .title(dto.getTitle())
                .productUrl(dto.getProductUrl())
                .imageUrls(imageUrls)
                .description(dto.getDescription())
                .status(ProductRequestStatus.WAITING)
                .build();

        return ProductRequestDetailResDto.fromEntity(productRequestRepository.save(request));
    }

    @Transactional(readOnly = true)
    public Page<ProductRequestListResDto> getAllRequests(Pageable pageable) {
        Pageable sorted = forceCreatedAtDesc(pageable);
        return productRequestRepository.findAll(sorted)
                .map(ProductRequestListResDto::fromEntity);
    }

    @Transactional(readOnly = true)
    public Page<ProductRequestListResDto> getAllRequestsByStatus(ProductRequestStatus status, Pageable pageable) {
        Pageable sorted = forceCreatedAtDesc(pageable);

        Page<ProductRequest> page = (status == null)
                ? productRequestRepository.findAll(sorted)
                : productRequestRepository.findAllByStatus(status, sorted);

        return page.map(ProductRequestListResDto::fromEntity);
    }


    @Transactional(readOnly = true)
    public Page<ProductRequestListResDto> getRequestsByMember(PrincipalDetails userDetails, Pageable pageable) {
        Long memberId = userDetails.getMember().getMemberId();
        Pageable sorted = forceCreatedAtDesc(pageable);
        return productRequestRepository.findByMemberMemberId(memberId, sorted)
                .map(ProductRequestListResDto::fromEntity);
    }

    @Transactional(readOnly = true)
    public ProductRequestDetailResDto getRequest(Long productRequestId) {
        ProductRequest request = getRequestOrThrow(productRequestId);
        return ProductRequestDetailResDto.fromEntity(request);
    }

    @Transactional
    public ProductRequestDetailResDto updateRequest(Long productRequestId, ProductRequestUpdateReqDto dto, List<MultipartFile> imageFiles, PrincipalDetails userDetails) throws IOException {

        ProductRequest request = getRequestOrThrow(productRequestId);
        Member member = getMember(userDetails);

        if (dto == null && imageFiles == null) {
            throw new CustomException(ProductRequestErrorCode.INVALID_PRODUCT_REQUEST_STATUS);
        }

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

        if (dto.getDescription() != null) {
            request.setDescription(dto.getDescription());
        }

        // 이미지 처리
        if (imageFiles != null && !imageFiles.isEmpty()) {
            // 새 이미지가 있으면 기존 이미지 삭제 후 새로 업로드
            if (request.getImageUrls() != null && !request.getImageUrls().isEmpty()) {
                imageUtil.deleteImages(request.getImageUrls());
            }
            List<String> uploadedUrls = imageUtil.saveImages(imageFiles, ImageType.PRODUCT);
            request.setImageUrls(uploadedUrls);
        } else {
            // 새 이미지 없고 기존 이미지도 없으면 예외
            if (request.getImageUrls() == null || request.getImageUrls().isEmpty()) {
                throw new CustomException(ProductRequestErrorCode.PRODUCT_IMAGE_NOT_FOUND);
            }
        }

        return ProductRequestDetailResDto.fromEntity(productRequestRepository.save(request));
    }

    @Transactional
    public ProductRequestDetailResDto updateStatus(Long productRequestId, String confirm, String message) {
        ProductRequest request = getRequestOrThrow(productRequestId);

        ProductRequestStatus newStatus = switch (confirm.toLowerCase()) {
            case "approve" -> {
                notificationPublisher.publish(NotificationTemplateType.REQUEST_APPROVED, productRequestId);
                yield ProductRequestStatus.APPROVED;
            }
            case "reject" -> {
                notificationPublisher.publish(NotificationTemplateType.REQUEST_REJECTED, productRequestId, message);
                yield ProductRequestStatus.REJECTED;
            }
            default -> throw new CustomException(ProductRequestErrorCode.INVALID_PRODUCT_REQUEST_STATUS);
        };

        request.changeStatus(newStatus);

        // ✅ 승인 시 Product 자동 생성
        if (newStatus == ProductRequestStatus.APPROVED) {
            Product product = Product.create(
                    request.getCategory(),
                    request.getTitle(),
                    request.getDescription(),
                    request.getImageUrls(),
                    0 // price 기본값 하드코딩
            );
            productRepository.save(product);
        }

        return ProductRequestDetailResDto.fromEntity(request);
    }


    @Transactional
    public void deleteRequest(Long productRequestId, PrincipalDetails userDetails) throws IOException {
        ProductRequest request = getRequestOrThrow(productRequestId);
        Member member = getMember(userDetails);

        if (!request.getMember().equals(member)) {
            throw new CustomException(ProductRequestErrorCode.PRODUCT_REQUEST_ACCESS_DENIED);
        }

        // S3에 저장된 이미지 삭제
        List<String> imageUrls = request.getImageUrls();
        if (imageUrls != null && !imageUrls.isEmpty()) {
            imageUtil.deleteImages(imageUrls);
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
