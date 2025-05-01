package com.team5.backend.domain.history.service;

import com.team5.backend.domain.groupBuy.entity.GroupBuy;
import com.team5.backend.domain.groupBuy.repository.GroupBuyRepository;
import com.team5.backend.domain.groupBuy.service.GroupBuyService;
import com.team5.backend.domain.history.dto.HistoryCreateReqDto;
import com.team5.backend.domain.history.dto.HistoryResDto;
import com.team5.backend.domain.history.dto.HistoryUpdateReqDto;
import com.team5.backend.domain.history.entity.History;
import com.team5.backend.domain.history.repository.HistoryRepository;
import com.team5.backend.domain.member.member.entity.Member;
import com.team5.backend.domain.member.member.repository.MemberRepository;
import com.team5.backend.domain.product.entity.Product;
import com.team5.backend.domain.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class HistoryService {

    private final HistoryRepository historyRepository;
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;
    private final GroupBuyRepository groupBuyRepository;

    public HistoryResDto createHistory(HistoryCreateReqDto request) {
        Member member = memberRepository.findById(request.getMemberId())
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
        return HistoryResDto.fromEntity(saved); // ✅ 한 줄 변환
    }

    public Page<HistoryResDto> getAllHistories(Pageable pageable) {
        Pageable sortedPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(Sort.Order.desc("createdAt")) // 최신순 정렬
        );

        return historyRepository.findAll(sortedPageable)
                .map(HistoryResDto::fromEntity);
    }


    public Optional<HistoryResDto> getHistoryById(Long id) {
        return historyRepository.findById(id)
                .map(HistoryResDto::fromEntity); // ✅ 한 줄 변환
    }

    public HistoryResDto updateHistory(Long id, HistoryUpdateReqDto request) {
        return historyRepository.findById(id)
                .map(existing -> {
                    existing.setWritable(request.getWritable());
                    History updated = historyRepository.save(existing);
                    return HistoryResDto.fromEntity(updated); // ✅ 한 줄 변환
                })
                .orElseThrow(() -> new RuntimeException("History not found with id " + id));
    }

    public void deleteHistory(Long id) {
        historyRepository.deleteById(id);
    }
}
