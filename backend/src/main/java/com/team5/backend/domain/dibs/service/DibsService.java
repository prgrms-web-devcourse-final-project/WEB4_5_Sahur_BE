package com.team5.backend.domain.dibs.service;

import com.team5.backend.domain.dibs.dto.DibsCreateReqDto;
import com.team5.backend.domain.dibs.dto.DibsResDto;
import com.team5.backend.domain.dibs.entity.Dibs;
import com.team5.backend.domain.dibs.repository.DibsRepository;
import com.team5.backend.domain.member.member.entity.Member;
import com.team5.backend.domain.member.member.repository.MemberRepository;
import com.team5.backend.domain.product.entity.Product;
import com.team5.backend.domain.product.repository.ProductRepository;
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

    public DibsResDto createDibs(DibsCreateReqDto request) {

        Member member = memberRepository.findById(request.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));

        Dibs dibs = Dibs.builder()
                .member(member)
                .product(product)
                .build();

        Dibs saved = dibsRepository.save(dibs);
        return DibsResDto.fromEntity(saved);
    }

    public Page<DibsResDto> getPagedDibsByMemberId(Long memberId, Pageable pageable) {
        return dibsRepository.findByMember_MemberId(memberId, pageable)
                .map(DibsResDto::fromEntity);
    }

    public List<DibsResDto> getAllDibsByMemberId(Long memberId) {
        return dibsRepository.findByMember_MemberId(memberId).stream()
                .map(DibsResDto::fromEntity)
                .collect(Collectors.toList());
    }

    public void deleteByProductAndMember(Long productId, Long memberId) {
        Dibs dibs = dibsRepository.findByProduct_ProductIdAndMember_MemberId(productId, memberId)
                .orElseThrow(() -> new IllegalArgumentException("해당 관심상품이 존재하지 않습니다."));
        dibsRepository.delete(dibs);
    }


}
