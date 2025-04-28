package com.team5.backend.domain.history.service;

import com.team5.backend.domain.history.dto.HistoryCreateReqDto;
import com.team5.backend.domain.history.dto.HistoryResDto;
import com.team5.backend.domain.history.dto.HistoryUpdateReqDto;
import com.team5.backend.domain.history.entity.History;
import com.team5.backend.domain.history.repository.HistoryRepository;
import com.team5.backend.domain.member.entity.Member;
import com.team5.backend.domain.member.repository.MemberRepository;
import com.team5.backend.domain.product.entity.Product;
import com.team5.backend.domain.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
        return toResponse(savedHistory);
    }

    public List<HistoryResDto> getAllHistories() {
        return historyRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public Optional<HistoryResDto> getHistoryById(Long id) {
        return historyRepository.findById(id)
                .map(this::toResponse);
    }

    public HistoryResDto updateHistory(Long id, HistoryUpdateReqDto request) {
        return historyRepository.findById(id)
                .map(existingHistory -> {
                    existingHistory.setWritable(request.getWritable());
                    History updatedHistory = historyRepository.save(existingHistory);
                    return toResponse(updatedHistory);
                })
                .orElseThrow(() -> new RuntimeException("History not found with id " + id));
    }

    public void deleteHistory(Long id) {
        historyRepository.deleteById(id);
    }

    private HistoryResDto toResponse(History history) {
        return HistoryResDto.builder()
                .historyId(history.getHistoryId())
                .memberId(history.getMember().getMemberId())
                .productId(history.getProduct().getProductId())
                .writable(history.getWritable())
                .build();
    }
}
