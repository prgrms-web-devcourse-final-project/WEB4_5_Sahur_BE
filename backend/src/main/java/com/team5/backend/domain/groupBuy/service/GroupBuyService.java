package com.team5.backend.domain.groupBuy.service;

import com.team5.backend.domain.dibs.repository.DibsRepository;
import com.team5.backend.domain.groupBuy.dto.*;
import com.team5.backend.domain.groupBuy.entity.GroupBuy;
import com.team5.backend.domain.groupBuy.entity.GroupBuySortField;
import com.team5.backend.domain.groupBuy.entity.GroupBuyStatus;
import com.team5.backend.domain.groupBuy.repository.GroupBuyRepository;
import com.team5.backend.domain.history.repository.HistoryRepository;
import com.team5.backend.domain.product.entity.Product;
import com.team5.backend.domain.product.repository.ProductRepository;
import com.team5.backend.domain.review.repository.ReviewRepository;
import com.team5.backend.global.exception.CustomException;
import com.team5.backend.global.exception.code.GroupBuyErrorCode;
import com.team5.backend.global.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GroupBuyService {

    private final GroupBuyRepository groupBuyRepository;
    private final ProductRepository productRepository;
    private final HistoryRepository historyRepository;
    private final DibsRepository dibsRepository;
    private final ReviewRepository reviewRepository;
    private final JwtUtil jwtUtil;

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

    @Transactional
    public GroupBuyResDto createGroupBuy(GroupBuyCreateReqDto request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new CustomException(GroupBuyErrorCode.PRODUCT_NOT_FOUND));

        GroupBuy groupBuy = GroupBuy.builder()
                .product(product)
                .targetParticipants(request.getTargetParticipants())
                .currentParticipantCount(0)
                .round(request.getRound())
                .deadline(request.getDeadline())
                .status(GroupBuyStatus.ONGOING)
                .build();

        return toDto(groupBuyRepository.save(groupBuy));
    }

    @Transactional(readOnly = true)
    public Page<GroupBuyResDto> getAllONGINGGroupBuys(Pageable pageable, GroupBuySortField sortField) {
        return groupBuyRepository.findByStatus(GroupBuyStatus.ONGOING, createSortedPageable(pageable, sortField))
                .map(this::toDto);
    }

    @Transactional(readOnly = true)
    public Page<GroupBuyResDto> getAllGroupBuys(Pageable pageable, GroupBuySortField sortField) {
        return groupBuyRepository.findAll(createSortedPageable(pageable, sortField))
                .map(this::toDto);
    }

    @Transactional(readOnly = true)
    public Page<GroupBuyResDto> getTodayDeadlineGroupBuys(Pageable pageable, GroupBuySortField sortField) {
        LocalDateTime startOfToday = LocalDateTime.now().toLocalDate().atStartOfDay();
        LocalDateTime endOfToday = startOfToday.plusDays(1).minusNanos(1);

        return groupBuyRepository.findByDeadlineBetween(startOfToday, endOfToday, createSortedPageable(pageable, sortField))
                .map(this::toDto);
    }

    @Transactional(readOnly = true)
    public Page<GroupBuyResDto> getGroupBuysByToken(String token, Pageable pageable) {
        String rawToken = token.replace("Bearer ", "");

        if (jwtUtil.isTokenBlacklisted(rawToken)) {
            throw new CustomException(GroupBuyErrorCode.TOKEN_BLACKLISTED);
        }

        if (!jwtUtil.validateAccessTokenInRedis(jwtUtil.extractEmail(rawToken), rawToken)) {
            throw new CustomException(GroupBuyErrorCode.TOKEN_INVALID);
        }

        Long memberId = jwtUtil.extractMemberId(rawToken);

        return historyRepository.findDistinctGroupBuysByMemberId(memberId, PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(Sort.Direction.DESC, "createdAt")))
                .map(this::toDto);
    }

    @Transactional(readOnly = true)
    public GroupBuyDetailResDto getGroupBuyById(Long groupBuyId, String token) {
        GroupBuy groupBuy = groupBuyRepository.findById(groupBuyId)
                .orElseThrow(() -> new CustomException(GroupBuyErrorCode.GROUP_BUY_NOT_FOUND));

        boolean isTodayDeadline = groupBuy.getDeadline() != null &&
                groupBuy.getDeadline().toLocalDate().isEqual(LocalDateTime.now().toLocalDate());

        Double averageRate = reviewRepository.findAverageRatingByProductId(groupBuy.getProduct().getProductId());
        if (averageRate == null) averageRate = 0.0;

        boolean isDibs = false;

        if (token != null && token.startsWith("Bearer ")) {
            String rawToken = token.replace("Bearer ", "");

            if (jwtUtil.isTokenBlacklisted(rawToken)) {
                throw new CustomException(GroupBuyErrorCode.TOKEN_BLACKLISTED);
            }

            if (!jwtUtil.validateAccessTokenInRedis(jwtUtil.extractEmail(rawToken), rawToken)) {
                throw new CustomException(GroupBuyErrorCode.TOKEN_INVALID);
            }

            Long memberId = jwtUtil.extractMemberId(rawToken);
            isDibs = dibsRepository.findByProduct_ProductIdAndMember_MemberId(groupBuy.getProduct().getProductId(), memberId).isPresent();
        }

        return GroupBuyDetailResDto.fromEntity(groupBuy, isTodayDeadline, isDibs, averageRate);
    }

    @Transactional
    public GroupBuyResDto updateGroupBuy(Long id, GroupBuyUpdateReqDto request) {
        return groupBuyRepository.findById(id)
                .map(existing -> {
                    existing.setTargetParticipants(request.getTargetParticipants());
                    existing.setCurrentParticipantCount(request.getCurrentParticipantCount());
                    existing.setRound(request.getRound());
                    existing.setDeadline(request.getDeadline());
                    existing.setStatus(request.getStatus());
                    return toDto(groupBuyRepository.save(existing));
                })
                .orElseThrow(() -> new CustomException(GroupBuyErrorCode.GROUP_BUY_NOT_FOUND));
    }

    @Transactional
    public GroupBuyResDto patchGroupBuy(Long id, GroupBuyPatchReqDto request) {
        GroupBuy existing = groupBuyRepository.findById(id)
                .orElseThrow(() -> new CustomException(GroupBuyErrorCode.GROUP_BUY_NOT_FOUND));

        if (request.getTargetParticipants() != null)
            existing.setTargetParticipants(request.getTargetParticipants());
        if (request.getCurrentParticipantCount() != null)
            existing.setCurrentParticipantCount(request.getCurrentParticipantCount());
        if (request.getRound() != null)
            existing.setRound(request.getRound());
        if (request.getDeadline() != null)
            existing.setDeadline(request.getDeadline());
        if (request.getStatus() != null)
            existing.setStatus(request.getStatus());

        return toDto(groupBuyRepository.save(existing));
    }

    @Transactional
    public void deleteGroupBuy(Long id) {
        groupBuyRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public GroupBuyStatusResDto getGroupBuyStatus(Long id) {
        return groupBuyRepository.findById(id)
                .map(GroupBuyStatusResDto::fromEntity)
                .orElseThrow(() -> new CustomException(GroupBuyErrorCode.GROUP_BUY_NOT_FOUND));
    }

    @Transactional
    public void closeGroupBuy(Long id) {
        GroupBuy groupBuy = groupBuyRepository.findById(id)
                .orElseThrow(() -> new CustomException(GroupBuyErrorCode.GROUP_BUY_NOT_FOUND));

        if (groupBuy.getStatus() == GroupBuyStatus.CLOSED) {
            throw new CustomException(GroupBuyErrorCode.GROUP_BUY_ALREADY_CLOSED);
        }

        groupBuy.setStatus(GroupBuyStatus.CLOSED);
    }

    @Transactional(readOnly = true)
    public List<GroupBuyResDto> getTop3GroupBuysByDibs() {
        return groupBuyRepository.findTop3ByDibsOrder(PageRequest.of(0, 3)).stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<GroupBuyResDto> getRandomTop3GroupBuysBySameCategory(Long groupBuyId) {
        GroupBuy groupBuy = groupBuyRepository.findById(groupBuyId)
                .orElseThrow(() -> new CustomException(GroupBuyErrorCode.GROUP_BUY_NOT_FOUND));

        Long categoryId = groupBuy.getProduct().getCategory().getCategoryId();

        return groupBuyRepository.findRandomTop3ByCategoryId(categoryId).stream()
                .map(this::toDto)
                .toList();
    }

    private GroupBuyResDto toDto(GroupBuy groupBuy) {
        boolean isDeadlineToday = groupBuy.getDeadline() != null &&
                groupBuy.getDeadline().toLocalDate().isEqual(LocalDate.now());
        return GroupBuyResDto.fromEntity(groupBuy, isDeadlineToday);
    }

    private Pageable createSortedPageable(Pageable pageable, GroupBuySortField sortField) {
        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), getSortForField(sortField));
    }

    private Sort getSortForField(GroupBuySortField sortField) {
        return switch (sortField) {
            case LATEST -> Sort.by(Sort.Order.desc("createdAt"));
            case POPULAR -> Sort.by(Sort.Order.desc("product.dibCount"));
            case DEADLINE_SOON -> Sort.by(Sort.Order.asc("deadline"));
            default -> Sort.unsorted();
        };
    }
}
