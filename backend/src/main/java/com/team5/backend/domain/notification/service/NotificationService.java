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
        return NotificationResDto.fromEntity(saved); // ✅ 한 줄 변환
    }

    public Page<NotificationResDto> getAllNotifications(Pageable pageable) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt"); // 최신순
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

        return notificationRepository.findAll(sortedPageable)
                .map(NotificationResDto::fromEntity); // ✅ 한 줄 변환
    }

    public Optional<NotificationResDto> getNotificationById(Long id) {
        return notificationRepository.findById(id)
                .map(NotificationResDto::fromEntity); // ✅ 한 줄 변환
    }

    public NotificationResDto updateNotification(Long id, NotificationUpdateReqDto request) {
        return notificationRepository.findById(id)
                .map(existing -> {
                    existing.setRead(request.getRead());
                    Notification updated = notificationRepository.save(existing);
                    return NotificationResDto.fromEntity(updated); // ✅ 한 줄 변환
                })
                .orElseThrow(() -> new RuntimeException("Notification not found with id " + id));
    }

    public void deleteNotification(Long id) {
        notificationRepository.deleteById(id);
    }
}
