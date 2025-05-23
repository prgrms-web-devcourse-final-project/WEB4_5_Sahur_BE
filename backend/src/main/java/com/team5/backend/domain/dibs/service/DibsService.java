package com.team5.backend.domain.dibs.service;

import com.team5.backend.domain.dibs.dto.DibsResDto;
import com.team5.backend.domain.dibs.entity.Dibs;
import com.team5.backend.domain.dibs.repository.DibsRepository;
import com.team5.backend.domain.member.member.entity.Member;
import com.team5.backend.domain.member.member.repository.MemberRepository;
import com.team5.backend.domain.product.entity.Product;
import com.team5.backend.domain.product.repository.ProductRepository;
import com.team5.backend.global.exception.CustomException;
import com.team5.backend.global.exception.code.DibsErrorCode;
import com.team5.backend.global.security.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DibsService {

    private final DibsRepository dibsRepository;
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;

    /**
     * 관심상품 생성
     */
    @Transactional
    public DibsResDto createDibs(Long productId, PrincipalDetails userDetails) {
        Long memberId = userDetails.getMember().getMemberId();

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(DibsErrorCode.DIBS_MEMBER_NOT_FOUND));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new CustomException(DibsErrorCode.DIBS_PRODUCT_NOT_FOUND));

        dibsRepository.findByProduct_ProductIdAndMember_MemberId(product.getProductId(), member.getMemberId())
                .ifPresent(dibs -> {
                    throw new CustomException(DibsErrorCode.DIBS_DUPLICATE);
                });

        Dibs dibs = Dibs.builder()
                .member(member)
                .product(product)
                .build();

        Dibs saved = dibsRepository.save(dibs);
        return DibsResDto.fromEntity(saved);
    }

    /**
     * 관심상품 삭제
     */
    @Transactional
    public void deleteByProductAndMember(Long productId, PrincipalDetails userDetails) {
        Long memberId = userDetails.getMember().getMemberId();

        Dibs dibs = dibsRepository.findByProduct_ProductIdAndMember_MemberId(productId, memberId)
                .orElseThrow(() -> new CustomException(DibsErrorCode.DIBS_NOT_FOUND));

        dibsRepository.delete(dibs);
    }

    /**
     * 관심상품 페이징 조회
     */
    @Transactional(readOnly = true)
    public Page<DibsResDto> getPagedDibsByMember(PrincipalDetails userDetails, Pageable pageable) {
        Long memberId = userDetails.getMember().getMemberId();
        Pageable sortedPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(Sort.Order.desc("createdAt"))
        );
        return dibsRepository.findPageWithProductByMemberId(memberId, sortedPageable)
                .map(DibsResDto::fromEntity);
    }

    /**
     * 관심상품 전체 조회
     */
    @Transactional(readOnly = true)
    public List<DibsResDto> getAllDibsByMember(PrincipalDetails userDetails) {
        Long memberId = userDetails.getMember().getMemberId();
        return dibsRepository.findAllWithProductByMemberId(memberId).stream()
                .map(DibsResDto::fromEntity)
                .toList();
    }
}
