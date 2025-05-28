package com.team5.backend.domain.dibs.service;

import com.team5.backend.domain.dibs.dto.DibsResDto;
import com.team5.backend.domain.dibs.entity.Dibs;
import com.team5.backend.domain.dibs.repository.DibsRepository;
import com.team5.backend.domain.groupBuy.dto.GroupBuyDto;
import com.team5.backend.domain.groupBuy.entity.GroupBuy;
import com.team5.backend.domain.groupBuy.entity.GroupBuyStatus;
import com.team5.backend.domain.groupBuy.repository.GroupBuyRepository;
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
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DibsService {

    private final DibsRepository dibsRepository;
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;
    private final GroupBuyRepository groupBuyRepository; // ✅ 추가

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
     * 관심상품 페이징 조회 + 공동구매 Ongoing 포함
     */
    @Transactional(readOnly = true)
    public Page<DibsResDto> getPagedDibsByMember(PrincipalDetails userDetails, Pageable pageable) {
        Long memberId = userDetails.getMember().getMemberId();
        Pageable sortedPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(Sort.Order.desc("createdAt"))
        );

        Page<Dibs> dibsPage = dibsRepository.findPageWithProductByMemberId(memberId, sortedPageable);

        // 상품 ID만 추출
        List<Long> productIds = dibsPage.stream()
                .map(dibs -> dibs.getProduct().getProductId())
                .distinct()
                .toList();

        // 해당 상품들에 대한 ONGOING 공동구매 미리 로딩
        List<GroupBuy> groupBuys = groupBuyRepository.findByProduct_ProductIdInAndStatus(productIds, GroupBuyStatus.ONGOING);

        // Map<productId, GroupBuy>
        Map<Long, GroupBuyDto> groupBuyMap = groupBuys.stream()
                .collect(Collectors.toMap(
                        gb -> gb.getProduct().getProductId(),
                        GroupBuyDto::fromEntity
                ));

        // 매핑된 groupBuyDto를 붙여 DibsResDto 생성
        return dibsPage.map(dibs -> {
            Long productId = dibs.getProduct().getProductId();
            GroupBuyDto groupBuyDto = groupBuyMap.get(productId); // 없으면 null
            return DibsResDto.fromEntity(dibs, groupBuyDto);
        });
    }



}
