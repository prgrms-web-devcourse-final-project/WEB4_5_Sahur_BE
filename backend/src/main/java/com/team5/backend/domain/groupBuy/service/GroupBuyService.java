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
import com.team5.backend.global.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GroupBuyService {

    private final GroupBuyRepository groupBuyRepository;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final HistoryRepository historyRepository;
    private final JwtUtil jwtUtil;

    // TODO : 커스텀 예외 처리 적용 필요

    /**
     * 매일 자정(00:00)에 마감일이 지난 공동구매의 상태를 CLOSED로 변경
     */
    @Transactional
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
    @Transactional
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

    private Sort getSortForField(GroupBuySortField sortField) {
        return switch (sortField) {
            case LATEST -> Sort.by(Sort.Order.desc("createdAt"));
            case POPULAR -> Sort.by(Sort.Order.desc("product.dibCount"));
            default -> Sort.unsorted();
        };
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
    @Transactional
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
    @Transactional
    public GroupBuyResDto patchGroupBuy(Long id, GroupBuyPatchReqDto request) {
        GroupBuy existing = groupBuyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("GroupBuy not found with id " + id));

        existing.setTargetParticipants(request.getTargetParticipants() != null ? request.getTargetParticipants() : existing.getTargetParticipants());
        existing.setCurrentParticipantCount(request.getCurrentParticipantCount() != null ? request.getCurrentParticipantCount() : existing.getCurrentParticipantCount());
        existing.setRound(request.getRound() != null ? request.getRound() : existing.getRound());
        existing.setDeadline(request.getDeadline() != null ? request.getDeadline() : existing.getDeadline());
        existing.setStatus(request.getStatus() != null ? request.getStatus() : existing.getStatus());

        GroupBuy updated = groupBuyRepository.save(existing);
        return GroupBuyResDto.fromEntity(updated);
    }

    /**
     * 공동구매 삭제
     */
    @Transactional
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
     * 특정 사용자(token)를 통한 공동구매 목록 조회
     */
    public Page<GroupBuyResDto> getGroupBuysByToken(String token, Pageable pageable) {
        String rawToken = token.replace("Bearer ", "");
        if (jwtUtil.isTokenBlacklisted(rawToken)) {
            throw new RuntimeException("로그아웃된 토큰입니다.");
        }

        if (!jwtUtil.validateAccessTokenInRedis(jwtUtil.extractEmail(rawToken), rawToken)) {
            throw new RuntimeException("유효하지 않은 토큰입니다.");
        }

        Long memberId = jwtUtil.extractMemberId(rawToken);

        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

        Page<GroupBuy> groupBuys = historyRepository.findDistinctGroupBuysByMemberId(memberId, sortedPageable);
        return groupBuys.map(GroupBuyResDto::fromEntity);
    }

    /**
     * 공동구매 상태 조회
     */
    public GroupBuyStatusResDto getGroupBuyStatus(Long id) {
        return groupBuyRepository.findById(id)
                .map(GroupBuyStatusResDto::fromEntity)
                .orElseThrow(() -> new RuntimeException("GroupBuy not found with id " + id));
    }
}
