package com.team5.backend.domain.notification.service;

import com.team5.backend.domain.member.member.entity.Member;
import com.team5.backend.domain.member.member.repository.MemberRepository;
import com.team5.backend.domain.notification.dto.NotificationCreateReqDto;
import com.team5.backend.domain.notification.dto.NotificationResDto;
import com.team5.backend.domain.notification.dto.NotificationUpdateReqDto;
import com.team5.backend.domain.notification.entity.Notification;
import com.team5.backend.domain.notification.repository.NotificationRepository;
import com.team5.backend.global.exception.CustomException;
import com.team5.backend.global.exception.code.NotificationErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final MemberRepository memberRepository;

    /**
     * 알림 생성
     */
    public NotificationResDto createNotification(NotificationCreateReqDto request) {
        Member member = memberRepository.findById(request.getMemberId())
                .orElseThrow(() -> new CustomException(NotificationErrorCode.MEMBER_NOT_FOUND));

        Notification notification = Notification.builder()
                .member(member)
                .type(request.getType())
                .title(request.getTitle())
                .message(request.getMessage())
                .url(request.getUrl())
                .read(false)
                .createdAt(LocalDateTime.now())
                .build();

        Notification saved = notificationRepository.save(notification);
        return NotificationResDto.fromEntity(saved);
    }

    /**
     * 전체 알림 목록 조회
     */
    public Page<NotificationResDto> getAllNotifications(Pageable pageable) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

        return notificationRepository.findAll(sortedPageable)
                .map(NotificationResDto::fromEntity);
    }

    /**
     * 알림 단건 조회
     */
    public NotificationResDto getNotificationById(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new CustomException(NotificationErrorCode.NOTIFICATION_NOT_FOUND));
        return NotificationResDto.fromEntity(notification);
    }


    /**
     * 알림 전체 업데이트
     */
    public NotificationResDto updateNotification(Long id, NotificationUpdateReqDto request) {
        return notificationRepository.findById(id)
                .map(existing -> {
                    existing.setRead(request.getRead());
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
    public void deleteNotification(Long id) {
        if (!notificationRepository.existsById(id)) {
            throw new CustomException(NotificationErrorCode.NOTIFICATION_NOT_FOUND);
        }
        notificationRepository.deleteById(id);
    }

    /**
     * 알림 읽음 상태 PATCH
     */
    public NotificationResDto patchNotification(Long id) {
        return notificationRepository.findById(id)
                .map(existing -> {
                    existing.setRead(true);
                    Notification updated = notificationRepository.save(existing);
                    return NotificationResDto.fromEntity(updated);
                })
                .orElseThrow(() -> new CustomException(NotificationErrorCode.NOTIFICATION_NOT_FOUND));
    }

    /**
     * 특정 회원의 알림 목록 조회
     */
    public Page<NotificationResDto> getNotificationsByMemberId(Long memberId, Pageable pageable) {
        if (!memberRepository.existsById(memberId)) {
            throw new CustomException(NotificationErrorCode.MEMBER_NOT_FOUND);
        }

        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

        return notificationRepository.findByMemberMemberId(memberId, sortedPageable)
                .map(NotificationResDto::fromEntity);
    }
}
