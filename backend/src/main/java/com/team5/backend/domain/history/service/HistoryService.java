package com.team5.backend.domain.history.service;

import com.team5.backend.domain.groupBuy.entity.GroupBuy;
import com.team5.backend.domain.groupBuy.repository.GroupBuyRepository;
import com.team5.backend.domain.history.dto.HistoryCreateReqDto;
import com.team5.backend.domain.history.dto.HistoryResDto;
import com.team5.backend.domain.history.dto.HistoryUpdateReqDto;
import com.team5.backend.domain.history.entity.History;
import com.team5.backend.domain.history.repository.HistoryRepository;
import com.team5.backend.domain.member.member.entity.Member;
import com.team5.backend.domain.member.member.repository.MemberRepository;
import com.team5.backend.domain.product.entity.Product;
import com.team5.backend.domain.product.repository.ProductRepository;
import com.team5.backend.global.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class HistoryService {

    private final HistoryRepository historyRepository;
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;
    private final GroupBuyRepository groupBuyRepository;
    private final JwtUtil jwtUtil;

    /**
     * 구매 이력 생성
     * memberId는 토큰에서 추출
     */
    @Transactional
    public HistoryResDto createHistory(HistoryCreateReqDto request, String token) {
        String rawToken = token.replace("Bearer ", "");

        if (jwtUtil.isTokenBlacklisted(rawToken)) {
            throw new RuntimeException("로그아웃된 토큰입니다.");
        }

        if (!jwtUtil.validateAccessTokenInRedis(jwtUtil.extractEmail(rawToken), rawToken)) {
            throw new RuntimeException("유효하지 않은 토큰입니다.");
        }

        Long memberId = jwtUtil.extractMemberId(rawToken);

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        GroupBuy groupBuy = groupBuyRepository.findById(request.getGroupBuyId())
                .orElseThrow(() -> new RuntimeException("GroupBuy not found"));

        History history = History.builder()
                .member(member)
                .product(product)
                .groupBuy(groupBuy)
                .writable(request.getWritable())
                .build();

        History saved = historyRepository.save(history);
        return HistoryResDto.fromEntity(saved);
    }

    /**
     * 전체 구매 이력 조회 (최신순)
     */
    public Page<HistoryResDto> getAllHistories(Pageable pageable) {
        Pageable sortedPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(Sort.Order.desc("createdAt"))
        );

        return historyRepository.findAll(sortedPageable)
                .map(HistoryResDto::fromEntity);
    }

    /**
     * 구매 이력 단건 조회
     */
    public Optional<HistoryResDto> getHistoryById(Long id) {
        return historyRepository.findById(id)
                .map(HistoryResDto::fromEntity);
    }

    /**
     * 구매 이력 writable 상태 업데이트
     */
    @Transactional
    public HistoryResDto updateHistory(Long id, HistoryUpdateReqDto request) {
        return historyRepository.findById(id)
                .map(existing -> {
                    existing.setWritable(request.getWritable());
                    History updated = historyRepository.save(existing);
                    return HistoryResDto.fromEntity(updated);
                })
                .orElseThrow(() -> new RuntimeException("History not found with id " + id));
    }

    /**
     * 구매 이력 삭제
     */
    @Transactional
    public void deleteHistory(Long id) {
        historyRepository.deleteById(id);
    }
}
