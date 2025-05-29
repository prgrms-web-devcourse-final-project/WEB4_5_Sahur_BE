package com.team5.backend.domain.notification.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.team5.backend.domain.dibs.repository.DibsRepository;
import com.team5.backend.domain.groupBuy.entity.GroupBuy;
import com.team5.backend.domain.groupBuy.repository.GroupBuyRepository;
import com.team5.backend.domain.member.member.entity.Member;
import com.team5.backend.domain.member.member.repository.MemberRepository;
import com.team5.backend.domain.notification.dto.NotificationCreateReqDto;
import com.team5.backend.domain.notification.dto.NotificationResDto;
import com.team5.backend.domain.notification.dto.NotificationUpdateReqDto;
import com.team5.backend.domain.notification.entity.Notification;
import com.team5.backend.domain.notification.redis.NotificationPublisher;
import com.team5.backend.domain.notification.repository.NotificationRepository;
import com.team5.backend.domain.notification.template.NotificationTemplateType;
import com.team5.backend.domain.order.repository.OrderRepository;
import com.team5.backend.global.exception.CustomException;
import com.team5.backend.global.exception.code.GroupBuyErrorCode;
import com.team5.backend.global.exception.code.NotificationErrorCode;
import com.team5.backend.global.security.PrincipalDetails;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final MemberRepository memberRepository;
    private final GroupBuyRepository groupBuyRepository;
    private final OrderRepository orderRepository;
    private final DibsRepository dibsRepository;

    private final NotificationPublisher notificationPublisher;

    /**
     * 알림 생성
     */
    @Transactional
    public NotificationResDto createNotification(NotificationCreateReqDto request, PrincipalDetails userDetails) {
        Long memberId = userDetails.getMember().getMemberId();

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(NotificationErrorCode.MEMBER_NOT_FOUND));

        Notification notification = Notification.builder()
                .member(member)
                .type(request.getType())
                .title(request.getTitle())
                .message(request.getMessage())
                .url(request.getUrl())
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();

        Notification saved = notificationRepository.save(notification);
        return NotificationResDto.fromEntity(saved);
    }

    /**
     * 전체 알림 목록 조회
     */
    @Transactional(readOnly = true)
    public Page<NotificationResDto> getAllNotifications(Pageable pageable) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

        return notificationRepository.findAll(sortedPageable)
                .map(NotificationResDto::fromEntity);
    }

    /**
     * 알림 단건 조회
     */
    @Transactional(readOnly = true)
    public NotificationResDto getNotificationById(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new CustomException(NotificationErrorCode.NOTIFICATION_NOT_FOUND));
        return NotificationResDto.fromEntity(notification);
    }

    /**
     * 알림 전체 업데이트
     */
    @Transactional
    public NotificationResDto updateNotification(Long id, NotificationUpdateReqDto request) {
        return notificationRepository.findById(id)
                .map(existing -> {
                    existing.setIsRead(request.getIsRead());
                    existing.setTitle(request.getTitle());
                    existing.setMessage(request.getMessage());
                    existing.setUrl(request.getUrl());
                    Notification updated = notificationRepository.save(existing);
                    return NotificationResDto.fromEntity(updated);
                })
                .orElseThrow(() -> new CustomException(NotificationErrorCode.NOTIFICATION_NOT_FOUND));
    }

    /**
     * 알림 삭제
     */
    @Transactional
    public void deleteNotification(Long id) {
        if (!notificationRepository.existsById(id)) {
            throw new CustomException(NotificationErrorCode.NOTIFICATION_NOT_FOUND);
        }
        notificationRepository.deleteById(id);
    }

    /**
     * 알림 읽음 상태 PATCH
     */
    @Transactional
    public NotificationResDto patchNotification(Long id) {
        return notificationRepository.findById(id)
                .map(existing -> {
                    existing.setIsRead(true);
                    Notification updated = notificationRepository.save(existing);
                    return NotificationResDto.fromEntity(updated);
                })
                .orElseThrow(() -> new CustomException(NotificationErrorCode.NOTIFICATION_NOT_FOUND));
    }

    /**
     * 특정 회원의 알림 목록 조회
     */
    @Transactional(readOnly = true)
    public Page<NotificationResDto> getNotificationsByMember(PrincipalDetails userDetails, Pageable pageable) {
        Long memberId = userDetails.getMember().getMemberId();

        if (!memberRepository.existsById(memberId)) {
            throw new CustomException(NotificationErrorCode.MEMBER_NOT_FOUND);
        }

        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

        return notificationRepository.findByMemberMemberId(memberId, sortedPageable)
                .map(NotificationResDto::fromEntity);
    }

    /**
     * 공동 구매 종료시 알림 일괄 생성
     */
    @Transactional
    public void groupBuyCloseNotifications(Long groupBuyId, String message) {
        groupBuyRepository.findById(groupBuyId)
                .orElseThrow(() -> new CustomException(GroupBuyErrorCode.GROUP_BUY_NOT_FOUND));

        List<Long> memberIds = orderRepository.findParticipantMemberIdsByGroupBuyId(groupBuyId);
        if (memberIds.isEmpty()) {
            return;
        }

        notificationPublisher.publish(
                NotificationTemplateType.GROUP_CLOSED,
                groupBuyId,
                memberIds,
                message
        );
    }

    /**
     * 관심 상품에서 공동구매 재오픈 알림 일괄 생성
     */
    @Transactional
    public void dibsReopenNotifications(Long groupBuyId) {
        GroupBuy groupBuy = groupBuyRepository.findById(groupBuyId)
                .orElseThrow(() -> new CustomException(GroupBuyErrorCode.GROUP_BUY_NOT_FOUND));
        Long productId = groupBuy.getProduct().getProductId();

        List<Long> memberIds = dibsRepository.findMemberIdsByProductId(productId);
        if (memberIds.isEmpty()) {
            return;
        }

        notificationPublisher.publish(
                NotificationTemplateType.DIBS_REOPENED,
                groupBuyId,
                memberIds
        );
    }

    /**
     * 관심 상품에서 공동구매 마감 임박 알림 일괄 생성
     */
    @Transactional
    public void dibsDeadlineNotifications() {
        // 현재 시각 + 1시간 범위 내에 마감되는 공동구매 조회
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneHourLater = now.plusHours(1);

        List<GroupBuy> expiringGroupBuys = groupBuyRepository.findByEndAtBetween(now, oneHourLater);

        for (GroupBuy groupBuy : expiringGroupBuys) {
            Long productId = groupBuy.getProduct().getProductId();

            List<Long> memberIds = dibsRepository.findMemberIdsByProductId(productId);
            if (memberIds.isEmpty()) continue;

            notificationPublisher.publish(
                    NotificationTemplateType.DIBS_DEADLINE,
                    productId,
                    memberIds
            );
        }
    }
}
