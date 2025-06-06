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
import com.team5.backend.domain.order.repository.OrderRepository;
import com.team5.backend.domain.product.entity.Product;
import com.team5.backend.domain.product.repository.ProductRepository;
import com.team5.backend.global.exception.CustomException;
import com.team5.backend.global.exception.code.HistoryErrorCode;
import com.team5.backend.global.security.PrincipalDetails;
import com.team5.backend.global.util.JwtUtil;
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
public class HistoryService {

    private final HistoryRepository historyRepository;
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;
    private final GroupBuyRepository groupBuyRepository;
    private final OrderRepository orderRepository;
    private final JwtUtil jwtUtil;

    /**
     * 구매 이력 생성
     * memberId는 토큰에서 추출
     */
    @Transactional
    public HistoryResDto createHistory(HistoryCreateReqDto request, PrincipalDetails userDetails) {

        Long memberId = userDetails.getMember().getMemberId();
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(HistoryErrorCode.MEMBER_NOT_FOUND));

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new CustomException(HistoryErrorCode.PRODUCT_NOT_FOUND));

        GroupBuy groupBuy = groupBuyRepository.findById(request.getGroupBuyId())
                .orElseThrow(() -> new CustomException(HistoryErrorCode.GROUP_BUY_NOT_FOUND));

        orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new CustomException(HistoryErrorCode.ORDER_NOT_FOUND));

        History history = History.builder()
                .member(member)
                .product(product)
                .groupBuy(groupBuy)
                .writable(request.getWritable())
                .build();

        return HistoryResDto.fromEntity(historyRepository.save(history));
    }

    /**
     * 전체 구매 이력 조회 (최신순)
     */
    @Transactional(readOnly = true)
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
    @Transactional(readOnly = true)
    public HistoryResDto getHistoryById(Long id) {
        return historyRepository.findById(id)
                .map(HistoryResDto::fromEntity)
                .orElseThrow(() -> new CustomException(HistoryErrorCode.HISTORY_NOT_FOUND));
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
                .orElseThrow(() -> new CustomException(HistoryErrorCode.HISTORY_NOT_FOUND));
    }

    /**
     * 구매 이력 삭제
     */
    @Transactional
    public void deleteHistory(Long id) {
        History history = historyRepository.findById(id)
                .orElseThrow(() -> new CustomException(HistoryErrorCode.HISTORY_NOT_FOUND));

        historyRepository.delete(history);
    }

    @Transactional(readOnly = true)
    public List<HistoryResDto> getWritableHistories(Long productId, PrincipalDetails userDetails) {
        Long memberId = userDetails.getMember().getMemberId();

        return historyRepository.findByMember_MemberIdAndProduct_ProductId(memberId, productId).stream()
                .filter(history -> Boolean.TRUE.equals(history.getWritable()))
                .map(HistoryResDto::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public Page<HistoryResDto> getMyWritableHistories(PrincipalDetails userDetails, Pageable pageable) {
        Long memberId = userDetails.getMember().getMemberId();

        return historyRepository.findByMember_MemberIdAndWritableTrue(memberId, pageable)
                .map(HistoryResDto::fromEntity);
    }



}
