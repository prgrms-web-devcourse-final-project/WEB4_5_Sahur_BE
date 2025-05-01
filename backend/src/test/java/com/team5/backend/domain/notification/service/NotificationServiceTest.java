package com.team5.backend.domain.notification.service;

import com.team5.backend.domain.member.member.entity.Member;
import com.team5.backend.domain.member.member.repository.MemberRepository;
import com.team5.backend.domain.notification.dto.*;
import com.team5.backend.domain.notification.entity.Notification;
import com.team5.backend.domain.notification.entity.NotificationType;
import com.team5.backend.domain.notification.repository.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class NotificationServiceTest {

    @Mock private NotificationRepository notificationRepository;
    @Mock private MemberRepository memberRepository;

    @InjectMocks
    private NotificationService notificationService;

    private Member member;
    private Notification notification;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        member = Member.builder()
                .memberId(1L)
                .email("test@team5.com")
                .nickname("테스터")
                .build();

        notification = Notification.builder()
                .notificationId(1L)
                .member(member)
                .type(NotificationType.ETC)
                .title("알림 제목")
                .message("알림 내용")
                .url("/test")
                .read(false)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("알림 생성")
    void createNotification() {
        NotificationCreateReqDto dto = NotificationCreateReqDto.builder()
                .memberId(1L)
                .type(NotificationType.ETC)
                .title("알림 제목")
                .message("알림 내용")
                .url("/test")
                .build();

        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(notificationRepository.save(any())).thenReturn(notification);

        NotificationResDto result = notificationService.createNotification(dto);

        assertEquals("알림 제목", result.getTitle());
        verify(notificationRepository).save(any());
    }

    @Test
    @DisplayName("전체 알림 조회")
    void getAllNotifications() {
        Pageable pageable = PageRequest.of(0, 5);
        when(notificationRepository.findAll(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(notification)));

        Page<NotificationResDto> result = notificationService.getAllNotifications(pageable);

        assertEquals(1, result.getTotalElements());
    }

    @Test
    @DisplayName("ID로 알림 단건 조회")
    void getNotificationById() {
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(notification));

        Optional<NotificationResDto> result = notificationService.getNotificationById(1L);

        assertTrue(result.isPresent());
        assertEquals("알림 제목", result.get().getTitle());
    }

    @Test
    @DisplayName("알림 전체 업데이트 (읽음, 제목, 내용, URL)")
    void updateNotification() {
        NotificationUpdateReqDto dto = NotificationUpdateReqDto.builder()
                .read(true)
                .title("변경된 제목")
                .message("변경된 내용")
                .url("/changed")
                .build();

        when(notificationRepository.findById(1L)).thenReturn(Optional.of(notification));
        when(notificationRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        NotificationResDto result = notificationService.updateNotification(1L, dto);

        assertTrue(result.getRead());
        assertEquals("변경된 제목", result.getTitle());
        assertEquals("변경된 내용", result.getMessage());
        assertEquals("/changed", result.getUrl());
        verify(notificationRepository).save(any());
    }

    @Test
    @DisplayName("알림 삭제")
    void deleteNotification() {
        notificationService.deleteNotification(1L);
        verify(notificationRepository).deleteById(1L);
    }

    @Test
    @DisplayName("알림 읽음 처리 (PATCH)")
    void patchNotification() {
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(notification));
        when(notificationRepository.save(any())).thenReturn(notification);

        NotificationResDto result = notificationService.patchNotification(1L);

        assertTrue(result.getRead());
    }

    @Test
    @DisplayName("회원별 알림 조회")
    void getNotificationsByMemberId() {
        Pageable pageable = PageRequest.of(0, 5);
        when(notificationRepository.findByMemberMemberId(eq(1L), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(notification)));

        Page<NotificationResDto> result = notificationService.getNotificationsByMemberId(1L, pageable);

        assertEquals(1, result.getTotalElements());
    }
}
