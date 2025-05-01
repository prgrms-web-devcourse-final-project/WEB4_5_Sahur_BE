package com.team5.backend.domain.notification.service;

import com.team5.backend.domain.member.member.entity.Member;
import com.team5.backend.domain.member.member.repository.MemberRepository;
import com.team5.backend.domain.notification.dto.NotificationCreateReqDto;
import com.team5.backend.domain.notification.dto.NotificationResDto;
import com.team5.backend.domain.notification.dto.NotificationUpdateReqDto;
import com.team5.backend.domain.notification.entity.Notification;
import com.team5.backend.domain.notification.repository.NotificationRepository;
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
     * @param request 알림 생성 요청 DTO
     * @return 생성된 알림 응답 DTO
     */
    public NotificationResDto createNotification(NotificationCreateReqDto request) {
        Member member = memberRepository.findById(request.getMemberId())
                .orElseThrow(() -> new RuntimeException("Member not found"));

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
     * 전체 알림 목록 조회 (최신순 정렬)
     * @param pageable 페이징 정보
     * @return 알림 목록 응답 DTO 페이지
     */
    public Page<NotificationResDto> getAllNotifications(Pageable pageable) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

        return notificationRepository.findAll(sortedPageable)
                .map(NotificationResDto::fromEntity);
    }

    /**
     * 알림 단건 조회
     * @param id 알림 ID
     * @return 해당 알림의 응답 DTO (Optional)
     */
    public Optional<NotificationResDto> getNotificationById(Long id) {
        return notificationRepository.findById(id)
                .map(NotificationResDto::fromEntity);
    }

    /**
     * 알림 읽음 상태 전체 업데이트 (PUT)
     * @param id 알림 ID
     * @param request 읽음 상태 DTO
     * @return 수정된 알림 응답 DTO
     */
    public NotificationResDto updateNotification(Long id, NotificationUpdateReqDto request) {
        return notificationRepository.findById(id)
                .map(existing -> {
                    existing.setRead(request.getRead());
                    Notification updated = notificationRepository.save(existing);
                    return NotificationResDto.fromEntity(updated);
                })
                .orElseThrow(() -> new RuntimeException("Notification not found with id " + id));
    }

    /**
     * 알림 삭제
     * @param id 알림 ID
     */
    public void deleteNotification(Long id) {
        notificationRepository.deleteById(id);
    }

    /**
     * 알림 읽음 상태 PATCH (부분 업데이트)
     * @param id 알림 ID
     * @return 수정된 알림 응답 DTO
     */
    public NotificationResDto patchNotification(Long id) {
        return notificationRepository.findById(id)
                .map(existing -> {
                    existing.setRead(true);
                    Notification updated = notificationRepository.save(existing);
                    return NotificationResDto.fromEntity(updated);
                })
                .orElseThrow(() -> new RuntimeException("Notification not found with id " + id));
    }

    /**
     * 특정 회원의 알림 목록 조회 (최신순 정렬)
     * @param memberId 회원 ID
     * @param pageable 페이징 정보
     * @return 해당 회원의 알림 목록 응답 DTO 페이지
     */
    public Page<NotificationResDto> getNotificationsByMemberId(Long memberId, Pageable pageable) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

        return notificationRepository.findByMemberId(memberId, sortedPageable)
                .map(NotificationResDto::fromEntity);
    }
}
