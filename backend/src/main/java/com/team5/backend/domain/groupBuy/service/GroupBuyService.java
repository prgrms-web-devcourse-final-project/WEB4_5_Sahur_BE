package com.team5.backend.domain.groupBuy.service;

import com.team5.backend.domain.category.entity.Category;
import com.team5.backend.domain.category.repository.CategoryRepository;
import com.team5.backend.domain.groupBuy.dto.*;
import com.team5.backend.domain.groupBuy.entity.GroupBuy;
import com.team5.backend.domain.groupBuy.entity.GroupBuySortField;
import com.team5.backend.domain.groupBuy.entity.GroupBuyStatus;
import com.team5.backend.domain.groupBuy.repository.GroupBuyRepository;
import com.team5.backend.domain.history.repository.HistoryRepository;
import com.team5.backend.domain.product.entity.Product;
import com.team5.backend.domain.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GroupBuyService {

    private final GroupBuyRepository groupBuyRepository;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    // TODO : 커스텀 예외 처리 적용 필요

    // 매일 정각(00:00)에 실행
    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")    
    public void updateGroupBuyStatuses() {
        List<GroupBuy> ongoingGroupBuys = groupBuyRepository.findByStatus(GroupBuyStatus.ONGOING);
        LocalDateTime now = LocalDateTime.now();

        for (GroupBuy groupBuy : ongoingGroupBuys) {
            if (groupBuy.getDeadline() != null && now.isAfter(groupBuy.getDeadline())) {
                groupBuy.setStatus(GroupBuyStatus.CLOSED);
            }
        }

        groupBuyRepository.saveAll(ongoingGroupBuys);
    }

    public GroupBuyResDto createGroupBuy(GroupBuyCreateReqDto request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        GroupBuy groupBuy = GroupBuy.builder()
                .product(product)
                .category(category)
                .targetParticipants(request.getTargetParticipants())
                .currentParticipantCount(0)
                .round(request.getRound())
                .deadline(request.getDeadline())
                .status(GroupBuyStatus.ONGOING)
                .build();

        GroupBuy saved = groupBuyRepository.save(groupBuy);
        return GroupBuyResDto.fromEntity(saved);
    }

    public Page<GroupBuyResDto> getAllGroupBuys(Pageable pageable, GroupBuySortField sortField) {
        Sort sort = getSortForField(sortField);
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

        Page<GroupBuy> pageResult = groupBuyRepository.findAll(sortedPageable);

        return pageResult.map(GroupBuyResDto::fromEntity);
    }

    private Sort getSortForField(GroupBuySortField sortField) {
        switch (sortField) {
            case LATEST:
                return Sort.by(Sort.Order.desc("createdAt"));
            case POPULAR:
                return Sort.by(Sort.Order.desc("product.dibCount"));
            default:
                return Sort.unsorted();
        }
    }

    public Optional<GroupBuyResDto> getGroupBuyById(Long id) {
        return groupBuyRepository.findById(id)
                .map(GroupBuyResDto::fromEntity);
    }

    public GroupBuyResDto updateGroupBuy(Long id, GroupBuyUpdateReqDto request) {
        return groupBuyRepository.findById(id)
                .map(existing -> {
                    existing.setTargetParticipants(request.getTargetParticipants());
                    existing.setCurrentParticipantCount(request.getCurrentParticipantCount());
                    existing.setRound(request.getRound());
                    existing.setDeadline(request.getDeadline());
                    existing.setStatus(request.getStatus());
                    GroupBuy updated = groupBuyRepository.save(existing);
                    return GroupBuyResDto.fromEntity(updated);
                })
                .orElseThrow(() -> new RuntimeException("GroupBuy not found with id " + id));
    }

    public GroupBuyResDto patchGroupBuy(Long id, GroupBuyPatchReqDto request) {
        return groupBuyRepository.findById(id)
                .map(existing -> {
                    if (request.getTargetParticipants() != null) {
                        existing.setTargetParticipants(request.getTargetParticipants());
                    }
                    if (request.getCurrentParticipantCount() != null) {
                        existing.setCurrentParticipantCount(request.getCurrentParticipantCount());
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
                    GroupBuy updated = groupBuyRepository.save(existing);
                    return GroupBuyResDto.fromEntity(updated);
                })
                .orElseThrow(() -> new RuntimeException("GroupBuy not found with id " + id));
    }

    public void deleteGroupBuy(Long id) {
        groupBuyRepository.deleteById(id);
    }

    public Page<GroupBuyResDto> getTodayDeadlineGroupBuys(Pageable pageable, GroupBuySortField sortField) {
        LocalDateTime startOfToday = LocalDateTime.now().toLocalDate().atStartOfDay();
        LocalDateTime endOfToday = startOfToday.plusDays(1).minusNanos(1);

        Sort sort = getSortForField(sortField);
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

        Page<GroupBuy> pageResult = groupBuyRepository.findByDeadlineBetween(startOfToday, endOfToday, sortedPageable);

        return pageResult.map(GroupBuyResDto::fromEntity);
    }


//    public Page<GroupBuyResDto> getGroupBuysByMemberId(Long memberId, Pageable pageable, GroupBuySortField sortField) {
//        Sort sort = getSortForField(sortField);
//        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
//
//        Page<Long> groupBuyIds = historyRepository.findByMemberId(memberId)
//                .stream()
//                .map(history -> history.getGroupBuy().getGroupBuyId())
//                .distinct()
//                .collect(Collectors.toList());
//
//        if (groupBuyIds.isEmpty()) {
//            return Page.empty(sortedPageable);
//        }
//
//        Page<GroupBuy> pageResult = groupBuyRepository.findByGroupBuyIdIn(groupBuyIds, sortedPageable);
//
//        return pageResult.map(GroupBuyResDto::fromEntity);
//    }


    public GroupBuyStatusResDto getGroupBuyStatus(Long id) {
        return groupBuyRepository.findById(id)
                .map(GroupBuyStatusResDto::fromEntity)
                .orElseThrow(() -> new RuntimeException("GroupBuy not found with id " + id));
    }
}

