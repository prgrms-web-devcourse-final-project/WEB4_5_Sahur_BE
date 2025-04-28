package com.team5.backend.domain.notification.service;

import com.team5.backend.domain.member.entity.Member;
import com.team5.backend.domain.member.repository.MemberRepository;
import com.team5.backend.domain.notification.dto.NotificationCreateReqDto;
import com.team5.backend.domain.notification.dto.NotificationResDto;
import com.team5.backend.domain.notification.dto.NotificationUpdateReqDto;
import com.team5.backend.domain.notification.entity.Notification;
import com.team5.backend.domain.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final MemberRepository memberRepository;

    public NotificationResDto createNotification(NotificationCreateReqDto request) {
        Member member = memberRepository.findById(request.getMemberId())
                .orElseThrow(() -> new RuntimeException("Member not found"));

        Notification notification = Notification.builder()
                .member(member)
                .type(request.getType())
                .title(request.getTitle())
                .message(request.getMessage())
                .url(request.getUrl())
                .read(false) // 생성 시 기본 false
                .createdAt(LocalDateTime.now())
                .build();

        Notification saved = notificationRepository.save(notification);
        return toResponse(saved);
    }

    public List<NotificationResDto> getAllNotifications() {
        return notificationRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public Optional<NotificationResDto> getNotificationById(Long id) {
        return notificationRepository.findById(id)
                .map(this::toResponse);
    }

    public NotificationResDto updateNotification(Long id, NotificationUpdateReqDto request) {
        return notificationRepository.findById(id)
                .map(existing -> {
                    existing.setRead(request.getRead());
                    Notification updated = notificationRepository.save(existing);
                    return toResponse(updated);
                })
                .orElseThrow(() -> new RuntimeException("Notification not found with id " + id));
    }

    public void deleteNotification(Long id) {
        notificationRepository.deleteById(id);
    }

    private NotificationResDto toResponse(Notification notification) {
        return NotificationResDto.builder()
                .notificationId(notification.getNotificationId())
                .memberId(notification.getMember().getMemberId())
                .type(notification.getType())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .url(notification.getUrl())
                .read(notification.getRead())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}
