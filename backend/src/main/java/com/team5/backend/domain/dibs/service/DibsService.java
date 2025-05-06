package com.team5.backend.domain.dibs.service;

import com.team5.backend.domain.dibs.dto.DibsCreateReqDto;
import com.team5.backend.domain.dibs.dto.DibsResDto;
import com.team5.backend.domain.dibs.entity.Dibs;
import com.team5.backend.domain.dibs.repository.DibsRepository;
import com.team5.backend.domain.member.member.entity.Member;
import com.team5.backend.domain.member.member.repository.MemberRepository;
import com.team5.backend.domain.product.entity.Product;
import com.team5.backend.domain.product.repository.ProductRepository;
import com.team5.backend.global.exception.CustomException;
import com.team5.backend.global.exception.code.DibsErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DibsService {

    private final DibsRepository dibsRepository;
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;

    /**
     * 관심상품 생성
     * - 이미 등록된 경우 예외 발생
     * - 회원/상품 존재 여부 검증 포함
     */
    public DibsResDto createDibs(DibsCreateReqDto request) {

        Member member = memberRepository.findById(request.getMemberId())
                .orElseThrow(() -> new CustomException(DibsErrorCode.DIBS_MEMBER_NOT_FOUND));

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new CustomException(DibsErrorCode.DIBS_PRODUCT_NOT_FOUND));

        dibsRepository.findByProduct_ProductIdAndMember_MemberId(product.getProductId(), member.getMemberId())
                .ifPresent(dibs -> { throw new CustomException(DibsErrorCode.DIBS_DUPLICATE); });

        Dibs dibs = Dibs.builder()
                .member(member)
                .product(product)
                .build();

        Dibs saved = dibsRepository.save(dibs);
        return DibsResDto.fromEntity(saved);
    }

    /**
     * 관심상품 페이징 조회 (memberId 기준)
     */
    public Page<DibsResDto> getPagedDibsByMemberId(Long memberId, Pageable pageable) {
        return dibsRepository.findByMember_MemberId(memberId, pageable)
                .map(DibsResDto::fromEntity);
    }

    /**
     * 관심상품 전체 조회 (memberId 기준)
     */
    public List<DibsResDto> getAllDibsByMemberId(Long memberId) {
        return dibsRepository.findByMember_MemberId(memberId).stream()
                .map(DibsResDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 관심상품 삭제
     * - memberId/ productId 조합 기준으로 확인 후 삭제
     */
    public void deleteByProductAndMember(Long productId, Long memberId) {
        Dibs dibs = dibsRepository.findByProduct_ProductIdAndMember_MemberId(productId, memberId)
                .orElseThrow(() -> new CustomException(DibsErrorCode.DIBS_NOT_FOUND));
        dibsRepository.delete(dibs);
    }

}
