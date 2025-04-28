package com.team5.backend.domain.groupBuy.service;

import com.team5.backend.domain.groupBuy.dto.GroupBuyCreateReqDto;
import com.team5.backend.domain.groupBuy.dto.GroupBuyResDto;
import com.team5.backend.domain.groupBuy.dto.GroupBuyUpdateReqDto;
import com.team5.backend.domain.groupBuy.entity.GroupBuy;
import com.team5.backend.domain.groupBuy.entity.GroupBuySortField;
import com.team5.backend.domain.groupBuy.entity.GroupBuyStatus;
import com.team5.backend.domain.groupBuy.repository.GroupBuyRepository;
import com.team5.backend.domain.product.entity.Product;
import com.team5.backend.domain.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    // 매일 정각(00:00)에 실행
    @Scheduled(cron = "0 0 0 * * *")
    public void updateGroupBuyStatuses() {
        List<GroupBuy> ongoingGroupBuys = groupBuyRepository.findByStatus(GroupBuyStatus.ONGOING);
        LocalDateTime now = LocalDateTime.now();

        for (GroupBuy groupBuy : ongoingGroupBuys) {
            if (groupBuy.getDeadline() != null && now.isAfter(groupBuy.getDeadline())) {
                groupBuy.setStatus(GroupBuyStatus.CLOSED);
            }
        }

        groupBuyRepository.saveAll(ongoingGroupBuys); // 변경된 것들 저장
    }

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

    public List<GroupBuyResDto> getAllGroupBuys(Pageable pageable, GroupBuySortField sortField) {
        // GroupBuySortField에 따른 정렬 설정
        Sort sort = getSortForField(sortField);

        // Pageable에 정렬 적용
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

        // 페이징과 정렬 적용
        Page<GroupBuy> pageResult = groupBuyRepository.findAll(sortedPageable);
        return pageResult.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
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

    public List<GroupBuyResDto> getTodayDeadlineGroupBuys(Pageable pageable, GroupBuySortField sortField) {
        // 오늘 00:00:00 ~ 오늘 23:59:59 구하기
        LocalDateTime startOfToday = LocalDateTime.now().toLocalDate().atStartOfDay();
        LocalDateTime endOfToday = startOfToday.plusDays(1).minusNanos(1);

        // 정렬 적용
        Sort sort = getSortForField(sortField);
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

        // deadline이 오늘인 것만 필터링해서 가져오기 (findByDeadlineBetween 사용)
        Page<GroupBuy> pageResult = groupBuyRepository.findByDeadlineBetween(startOfToday, endOfToday, sortedPageable);

        return pageResult.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
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
