package com.team5.backend.domain.history.service;

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
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class HistoryService {

    private final HistoryRepository historyRepository;
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;

    public HistoryResDto createHistory(HistoryCreateReqDto request) {
        Member member = memberRepository.findById(request.getMemberId())
                .orElseThrow(() -> new RuntimeException("Member not found"));

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        History history = History.builder()
                .member(member)
                .product(product)
                .writable(request.getWritable())
                .build();

        History savedHistory = historyRepository.save(history);
        return toHistoryResDto(savedHistory);
    }

    public Page<HistoryResDto> getAllHistories(Pageable pageable) {
        return historyRepository.findAll(pageable)
                .map(this::toHistoryResDto);
    }

    public Optional<HistoryResDto> getHistoryById(Long id) {
        return historyRepository.findById(id)
                .map(this::toHistoryResDto);
    }

    public HistoryResDto updateHistory(Long id, HistoryUpdateReqDto request) {
        return historyRepository.findById(id)
                .map(existingHistory -> {
                    existingHistory.setWritable(request.getWritable());
                    History updatedHistory = historyRepository.save(existingHistory);
                    return toHistoryResDto(updatedHistory);
                })
                .orElseThrow(() -> new RuntimeException("History not found with id " + id));
    }

    public void deleteHistory(Long id) {
        historyRepository.deleteById(id);
    }

    private HistoryResDto toHistoryResDto(History history) {
        return HistoryResDto.builder()
                .historyId(history.getHistoryId())
                .memberId(history.getMember().getMemberId())
                .productId(history.getProduct().getProductId())
                .writable(history.getWritable())
                .build();
    }
}
