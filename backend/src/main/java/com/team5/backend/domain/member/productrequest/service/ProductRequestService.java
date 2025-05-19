package com.team5.backend.domain.member.productrequest.service;

import com.team5.backend.domain.category.entity.Category;
import com.team5.backend.domain.category.repository.CategoryRepository;
import com.team5.backend.domain.member.productrequest.dto.ProductRequestCreateReqDto;
import com.team5.backend.domain.member.productrequest.dto.ProductRequestResDto;
import com.team5.backend.domain.member.productrequest.entity.ProductRequest;
import com.team5.backend.domain.member.productrequest.entity.ProductRequestStatus;
import com.team5.backend.domain.member.productrequest.repository.ProductRequestRepository;
import com.team5.backend.domain.member.member.entity.Member;
import com.team5.backend.domain.member.member.repository.MemberRepository;
import com.team5.backend.global.exception.CustomException;
import com.team5.backend.global.exception.code.MemberErrorCode;
import com.team5.backend.global.exception.code.ProductErrorCode;
import com.team5.backend.global.security.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductRequestService {

    private final MemberRepository memberRepository;
    private final ProductRequestRepository productRequestRepository;
    private final CategoryRepository categoryRepository;



    @Transactional
    public ProductRequestResDto createRequest(ProductRequestCreateReqDto requestDto, PrincipalDetails userDetails) {
        Long memberId = userDetails.getMember().getMemberId();
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(MemberErrorCode.MEMBER_NOT_FOUND));

        Category category = categoryRepository.findById(requestDto.getCategoryId())
                .orElseThrow(() -> new CustomException(ProductErrorCode.CATEGORY_NOT_FOUND));

        ProductRequest productRequest = ProductRequest.builder()
                .member(member)
                .category(category)
                .title(requestDto.getTitle())
                .productUrl(requestDto.getProductUrl())
                .imageUrls(requestDto.getImageUrls())
                .description(requestDto.getDescription())
                .status(ProductRequestStatus.WAITING)
                .build();
        ProductRequest savedProductRequest  = productRequestRepository.save(productRequest);
        return ProductRequestResDto.fromEntity(savedProductRequest );
    }

}
