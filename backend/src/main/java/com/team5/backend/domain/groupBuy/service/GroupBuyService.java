package com.team5.backend.domain.groupBuy.service;

import com.team5.backend.domain.groupBuy.dto.GroupBuyCreateReqDto;
import com.team5.backend.domain.groupBuy.dto.GroupBuyResDto;
import com.team5.backend.domain.groupBuy.dto.GroupBuyUpdateReqDto;
import com.team5.backend.domain.groupBuy.entity.GroupBuy;
import com.team5.backend.domain.groupBuy.entity.GroupBuyStatus;
import com.team5.backend.domain.groupBuy.repository.GroupBuyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GroupBuyService {

    private final GroupBuyRepository groupBuyRepository;

    public GroupBuyResDto createGroupBuy(GroupBuyCreateReqDto request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        GroupBuy groupBuy = GroupBuy.builder()
                .product(product)
                .category(category)
                .minParticipants(request.getMinParticipants())
                .currentParticipants(0) // 처음에는 0명
                .round(request.getRound())
                .deadline(request.getDeadline())
                .status(GroupBuyStatus.ONGOING) // 기본값 ONGOING
                .build();

        GroupBuy saved = groupBuyRepository.save(groupBuy);
        return toResponse(saved);
    }

    public List<GroupBuyResDto> getAllGroupBuys() {
        return groupBuyRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public Optional<GroupBuyResDto> getGroupBuyById(Long id) {
        return groupBuyRepository.findById(id)
                .map(this::toResponse);
    }

    public GroupBuyResDto updateGroupBuy(Long id, GroupBuyUpdateReqDto request) {
        return groupBuyRepository.findById(id)
                .map(existing -> {
                    existing.setMinParticipants(request.getMinParticipants());
                    existing.setCurrentParticipants(request.getCurrentParticipants());
                    existing.setRound(request.getRound());
                    existing.setDeadline(request.getDeadline());
                    existing.setStatus(request.getStatus());
                    GroupBuy updated = groupBuyRepository.save(existing);
                    return toResponse(updated);
                })
                .orElseThrow(() -> new RuntimeException("GroupBuy not found with id " + id));
    }

    public void deleteGroupBuy(Long id) {
        groupBuyRepository.deleteById(id);
    }

    private GroupBuyResDto toResponse(GroupBuy groupBuy) {
        return GroupBuyResDto.builder()
                .groupBuyId(groupBuy.getGroupBuyId())
                .productId(groupBuy.getProduct().getProductId())
                .categoryId(groupBuy.getCategory().getCategoryId())
                .minParticipants(groupBuy.getMinParticipants())
                .currentParticipants(groupBuy.getCurrentParticipants())
                .round(groupBuy.getRound())
                .deadline(groupBuy.getDeadline())
                .status(groupBuy.getStatus())
                .build();
    }
}
