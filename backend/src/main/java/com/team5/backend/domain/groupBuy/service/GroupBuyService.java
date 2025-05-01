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

@Service
@RequiredArgsConstructor
public class GroupBuyService {

    private final GroupBuyRepository groupBuyRepository;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    // TODO : 커스텀 예외 처리 적용 필요

    /**
     * 매일 자정(00:00)에 마감일이 지난 공동구매의 상태를 CLOSED로 변경
     */
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

    /**
     * 새로운 공동구매 생성
     */
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

    /**
     * 전체 공동구매 목록 조회 + 정렬
     */
    public Page<GroupBuyResDto> getAllGroupBuys(Pageable pageable, GroupBuySortField sortField) {
        Sort sort = getSortForField(sortField);
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
        Page<GroupBuy> pageResult = groupBuyRepository.findAll(sortedPageable);
        return pageResult.map(GroupBuyResDto::fromEntity);
    }

    /**
     * 정렬 필드에 따른 Sort 반환
     */
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

    /**
     * ID로 공동구매 단건 조회
     */
    public Optional<GroupBuyResDto> getGroupBuyById(Long id) {
        return groupBuyRepository.findById(id)
                .map(GroupBuyResDto::fromEntity);
    }

    /**
     * 공동구매 전체 업데이트
     */
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

    /**
     * 공동구매 부분 업데이트
     */
    public GroupBuyResDto patchGroupBuy(Long id, GroupBuyPatchReqDto request) {
        GroupBuy existing = groupBuyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("GroupBuy not found with id " + id));

        // patch 요청의 값이 있을 경우에만 수정
        Integer targetParticipants = request.getTargetParticipants() != null
                ? request.getTargetParticipants()
                : existing.getTargetParticipants();

        Integer currentCount = request.getCurrentParticipantCount() != null
                ? request.getCurrentParticipantCount()
                : existing.getCurrentParticipantCount();

        Integer round = request.getRound() != null
                ? request.getRound()
                : existing.getRound();

        LocalDateTime deadline = request.getDeadline() != null
                ? request.getDeadline()
                : existing.getDeadline();

        GroupBuyStatus status = request.getStatus() != null
                ? request.getStatus()
                : existing.getStatus();

        // 엔티티 수정
        existing.setTargetParticipants(targetParticipants);
        existing.setCurrentParticipantCount(currentCount);
        existing.setRound(round);
        existing.setDeadline(deadline);
        existing.setStatus(status);

        GroupBuy updated = groupBuyRepository.save(existing);
        return GroupBuyResDto.fromEntity(updated);
    }


    /**
     * 공동구매 삭제
     */
    public void deleteGroupBuy(Long id) {
        groupBuyRepository.deleteById(id);
    }

    /**
     * 오늘 마감인 공동구매 조회
     */
    public Page<GroupBuyResDto> getTodayDeadlineGroupBuys(Pageable pageable, GroupBuySortField sortField) {
        LocalDateTime startOfToday = LocalDateTime.now().toLocalDate().atStartOfDay();
        LocalDateTime endOfToday = startOfToday.plusDays(1).minusNanos(1);

        Sort sort = getSortForField(sortField);
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

        Page<GroupBuy> pageResult = groupBuyRepository.findByDeadlineBetween(startOfToday, endOfToday, sortedPageable);
        return pageResult.map(GroupBuyResDto::fromEntity);
    }

    /**
     * 특정 사용자(memberId)가 참여한 공동구매 목록 조회 (최신순 정렬 포함)
     */
    public Page<GroupBuyResDto> getGroupBuysByMemberId(Long memberId, Pageable pageable) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

        Page<GroupBuy> groupBuys = historyRepository.findDistinctGroupBuysByMemberId(memberId, sortedPageable);
        return groupBuys.map(GroupBuyResDto::fromEntity);
    }

    /**
     * 공동구매 상태 조회 (예: 진행 중, 마감됨 등)
     */
    public GroupBuyStatusResDto getGroupBuyStatus(Long id) {
        return groupBuyRepository.findById(id)
                .map(GroupBuyStatusResDto::fromEntity)
                .orElseThrow(() -> new RuntimeException("GroupBuy not found with id " + id));
    }


}
