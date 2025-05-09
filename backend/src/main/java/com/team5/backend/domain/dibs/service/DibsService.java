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
import com.team5.backend.global.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DibsService {

    private final DibsRepository dibsRepository;
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;
    private final JwtUtil jwtUtil;

    /**
     * 관심상품 생성 (토큰에서 memberId 추출)
     */
    @Transactional
    public DibsResDto createDibs(Long productId, String token) {
        Long memberId = extractMemberIdFromToken(token);

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
     * 관심상품 페이징 조회 (토큰에서 memberId 추출)
     */
    @Transactional(readOnly = true)
    public Page<DibsResDto> getPagedDibsByToken(String token, Pageable pageable) {
        Long memberId = extractMemberIdFromToken(token);

        Pageable sortedPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(Sort.Order.desc("createdAt"))
        );

        return dibsRepository.findByMember_MemberId(memberId, sortedPageable)
                .map(DibsResDto::fromEntity);
    }

    /**
     * 관심상품 전체 조회 (토큰에서 memberId 추출)
     */
    @Transactional(readOnly = true)
    public List<DibsResDto> getAllDibsByToken(String token) {
        Long memberId = extractMemberIdFromToken(token);

        return dibsRepository.findByMember_MemberId(memberId).stream()
                .map(DibsResDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 관심상품 삭제 (토큰에서 memberId 추출)
     */
    @Transactional
    public void deleteByProductAndToken(Long productId, String token) {
        Long memberId = extractMemberIdFromToken(token);

        Dibs dibs = dibsRepository.findByProduct_ProductIdAndMember_MemberId(productId, memberId)
                .orElseThrow(() -> new CustomException(DibsErrorCode.DIBS_NOT_FOUND));

        dibsRepository.delete(dibs);
    }

    /**
     * JWT 토큰에서 memberId 추출 (공통 처리)
     */
    private Long extractMemberIdFromToken(String token) {
        String rawToken = token.replace("Bearer ", "");

        if (jwtUtil.isTokenBlacklisted(rawToken)) {
            throw new CustomException(DibsErrorCode.DIBS_TOKEN_BLACKLISTED);
        }

        if (!jwtUtil.validateAccessTokenInRedis(jwtUtil.extractEmail(rawToken), rawToken)) {
            throw new CustomException(DibsErrorCode.DIBS_TOKEN_INVALID);
        }

        return jwtUtil.extractMemberId(rawToken);
    }

}
 
