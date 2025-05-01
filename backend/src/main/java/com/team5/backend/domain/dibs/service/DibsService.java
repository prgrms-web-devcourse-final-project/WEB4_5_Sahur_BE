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
                .status(true)
                .build();

        Dibs saved = dibsRepository.save(dibs);
        return toResponse(saved);
    }

    public List<DibsResDto> getAllDibs() {
        return dibsRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public void deleteDibs(Long dibsId) {
        dibsRepository.deleteById(dibsId);
    }

    private DibsResDto toResponse(Dibs dibs) {
        return DibsResDto.builder()
                .dibsId(dibs.getDibsId())
                .memberId(dibs.getMember().getMemberId())
                .productId(dibs.getProduct().getProductId())
                .status(dibs.getStatus())
                .build();
    }
}
