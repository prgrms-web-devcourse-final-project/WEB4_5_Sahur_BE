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
import com.team5.backend.global.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final MemberRepository memberRepository;
    private final JwtUtil jwtUtil;

    /**
     * 알림 생성
     */
    @Transactional
    public NotificationResDto createNotification(NotificationCreateReqDto request, String token) {
        String rawToken = token.replace("Bearer ", "");

        if (jwtUtil.isTokenBlacklisted(rawToken)) {
            throw new CustomException(NotificationErrorCode.TOKEN_BLACKLISTED);
        }

        if (!jwtUtil.validateAccessTokenInRedis(jwtUtil.extractEmail(rawToken), rawToken)) {
            throw new CustomException(NotificationErrorCode.TOKEN_INVALID);
        }

        Long memberId = jwtUtil.extractMemberId(rawToken);

        Member member = memberRepository.findById(memberId)
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
    @Transactional
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
                    existing.setRead(true);
                    Notification updated = notificationRepository.save(existing);
                    return NotificationResDto.fromEntity(updated);
                })
                .orElseThrow(() -> new CustomException(NotificationErrorCode.NOTIFICATION_NOT_FOUND));
    }

    /**
     * 특정 회원의 알림 목록 조회 (토큰 기반)
     */
    public Page<NotificationResDto> getNotificationsByMemberToken(String token, Pageable pageable) {
        String rawToken = token.replace("Bearer ", "");

        if (jwtUtil.isTokenBlacklisted(rawToken)) {
            throw new CustomException(NotificationErrorCode.TOKEN_BLACKLISTED);
        }

        if (!jwtUtil.validateAccessTokenInRedis(jwtUtil.extractEmail(rawToken), rawToken)) {
            throw new CustomException(NotificationErrorCode.TOKEN_INVALID);
        }

        Long memberId = jwtUtil.extractMemberId(rawToken);

        if (!memberRepository.existsById(memberId)) {
            throw new CustomException(NotificationErrorCode.MEMBER_NOT_FOUND);
        }

        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

        return notificationRepository.findByMemberMemberId(memberId, sortedPageable)
                .map(NotificationResDto::fromEntity);
    }
}
