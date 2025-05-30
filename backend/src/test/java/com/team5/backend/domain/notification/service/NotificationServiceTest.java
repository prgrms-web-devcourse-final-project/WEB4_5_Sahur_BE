package com.team5.backend.domain.notification.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.team5.backend.domain.member.member.entity.Member;
import com.team5.backend.domain.member.member.repository.MemberRepository;
import com.team5.backend.domain.notification.dto.NotificationCreateReqDto;
import com.team5.backend.domain.notification.dto.NotificationResDto;
import com.team5.backend.domain.notification.dto.NotificationUpdateReqDto;
import com.team5.backend.domain.notification.entity.Notification;
import com.team5.backend.domain.notification.entity.NotificationType;
import com.team5.backend.domain.notification.repository.NotificationRepository;
import com.team5.backend.global.security.PrincipalDetails;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;
    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private NotificationService notificationService;

    private Member member;
    private Notification notification;
    private PrincipalDetails userDetails;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        member = Member.builder()
                .memberId(1L)
                .email("test@team5.com")
                .nickname("테스터")
                .build();

        userDetails = new PrincipalDetails(member, Map.of());

        notification = Notification.builder()
                .notificationId(1L)
                .member(member)
                .type(NotificationType.SYSTEM)
                .title("알림 제목")
                .message("알림 내용")
                .url("/test")
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("알림 생성")
    void createNotification() {
        NotificationCreateReqDto dto = NotificationCreateReqDto.builder()
                .type(NotificationType.SYSTEM)
                .title("알림 제목")
                .message("알림 내용")
                .url("/test")
                .build();

        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(notificationRepository.save(any())).thenReturn(notification);

        NotificationResDto result = notificationService.createNotification(dto, userDetails);

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

        NotificationResDto result = notificationService.getNotificationById(userDetails, 1L);

        assertEquals("알림 제목", result.getTitle());
    }

    @Test
    @DisplayName("알림 전체 업데이트")
    void updateNotification() {
        NotificationUpdateReqDto dto = NotificationUpdateReqDto.builder()
                .isRead(true)
                .title("변경된 제목")
                .message("변경된 내용")
                .url("/changed")
                .build();

        when(notificationRepository.findById(1L)).thenReturn(Optional.of(notification));
        when(notificationRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        NotificationResDto result = notificationService.updateNotification(1L, dto);

        assertTrue(result.getIsRead());
        assertEquals("변경된 제목", result.getTitle());
    }

    @Test
    @DisplayName("알림 삭제")
    void deleteNotification() {
        when(notificationRepository.existsById(1L)).thenReturn(true);

        notificationService.deleteNotification(1L);

        verify(notificationRepository).deleteById(1L);
    }

    @Test
    @DisplayName("알림 읽음 처리")
    void patchNotification() {
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(notification));
        when(notificationRepository.save(any())).thenReturn(notification);

        NotificationResDto result = notificationService.patchNotification(1L);

        assertTrue(result.getIsRead());
    }

    @Test
    @DisplayName("회원 기반 알림 조회")
    void getNotificationsByMember() {
        Pageable pageable = PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "createdAt"));

        when(memberRepository.existsById(1L)).thenReturn(true);
        when(notificationRepository.findByMemberMemberId(eq(1L), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(notification)));

        Page<NotificationResDto> result = notificationService.getNotificationsByMember(userDetails, pageable);

        assertEquals(1, result.getTotalElements());
        verify(notificationRepository).findByMemberMemberId(eq(1L), any(Pageable.class));
    }
}
