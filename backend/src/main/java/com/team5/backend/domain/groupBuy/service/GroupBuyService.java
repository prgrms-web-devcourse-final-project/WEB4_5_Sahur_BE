package com.team5.backend.domain.groupBuy.service;

import com.team5.backend.domain.category.entity.Category;
import com.team5.backend.domain.category.repository.CategoryRepository;
import com.team5.backend.domain.groupBuy.dto.GroupBuyCreateReqDto;
import com.team5.backend.domain.groupBuy.dto.GroupBuyResDto;
import com.team5.backend.domain.groupBuy.dto.GroupBuyUpdateReqDto;
import com.team5.backend.domain.groupBuy.entity.GroupBuy;
import com.team5.backend.domain.groupBuy.entity.GroupBuyStatus;
import com.team5.backend.domain.groupBuy.repository.GroupBuyRepository;
import com.team5.backend.domain.product.entity.Product;
import com.team5.backend.domain.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GroupBuyService {

    private final GroupBuyRepository groupBuyRepository;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

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

    public GroupBuyResDto patchGroupBuy(Long id, GroupBuyUpdateReqDto request) {
        return groupBuyRepository.findById(id)
                .map(existing -> {
                    // 제공된 값만 업데이트 (null 값은 업데이트하지 않음)
                    if (request.getMinParticipants() != null) {
                        existing.setMinParticipants(request.getMinParticipants());
                    }
                    if (request.getCurrentParticipants() != null) {
                        existing.setCurrentParticipants(request.getCurrentParticipants());
                    }
                    if (request.getRound() != null) {
                        existing.setRound(request.getRound());
                    }
                    if (request.getDeadline() != null) {
                        existing.setDeadline(request.getDeadline());
                    }
                    if (request.getStatus() != null) {
                        existing.setStatus(request.getStatus());
                    }

                    // 수정된 엔티티 저장
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
//                .categoryId(groupBuy.getCategory().getCategoryId())
                .minParticipants(groupBuy.getMinParticipants())
                .currentParticipants(groupBuy.getCurrentParticipants())
                .round(groupBuy.getRound())
                .deadline(groupBuy.getDeadline())
                .status(groupBuy.getStatus())
                .build();
    }
}
