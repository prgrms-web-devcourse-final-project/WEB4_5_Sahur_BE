package com.team5.backend.domain.notification.service;

import com.team5.backend.domain.member.member.entity.Member;
import com.team5.backend.domain.member.member.repository.MemberRepository;
import com.team5.backend.domain.notification.dto.*;
import com.team5.backend.domain.notification.entity.Notification;
import com.team5.backend.domain.notification.entity.NotificationType;
import com.team5.backend.domain.notification.repository.NotificationRepository;
import com.team5.backend.global.util.JwtUtil;
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
    @Mock private JwtUtil jwtUtil;

    @InjectMocks
    private NotificationService notificationService;

    private Member member;
    private Notification notification;
    private final String token = "Bearer valid.token.here";
    private final String rawToken = "valid.token.here";

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
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();

        // 공통 mock 설정
        when(jwtUtil.isTokenBlacklisted(rawToken)).thenReturn(false);
        when(jwtUtil.validateAccessTokenInRedis(eq("test@team5.com"), eq(rawToken))).thenReturn(true);
        when(jwtUtil.extractEmail(rawToken)).thenReturn("test@team5.com");
        when(jwtUtil.extractMemberId(rawToken)).thenReturn(1L);
    }

    @Test
    @DisplayName("알림 생성")
    void createNotification() {
        NotificationCreateReqDto dto = NotificationCreateReqDto.builder()
                .type(NotificationType.ETC)
                .title("알림 제목")
                .message("알림 내용")
                .url("/test")
                .build();

        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(notificationRepository.save(any())).thenReturn(notification);

        NotificationResDto result = notificationService.createNotification(dto, token);

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

        NotificationResDto result = notificationService.getNotificationById(1L);

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
    @DisplayName("토큰 기반 회원 알림 조회")
    void getNotificationsByMemberToken() {
        // 정렬 조건을 포함한 pageable: createdAt 내림차순
        Pageable pageable = PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "createdAt"));

        when(memberRepository.existsById(1L)).thenReturn(true);
        when(notificationRepository.findByMemberMemberId(eq(1L), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(notification)));

        Page<NotificationResDto> result = notificationService.getNotificationsByMemberToken(token, pageable);

        assertEquals(1, result.getTotalElements());

        // 정렬 포함된 pageable 객체가 일치하지 않을 수 있으므로 any(Pageable.class)로 검증
        verify(notificationRepository).findByMemberMemberId(eq(1L), any(Pageable.class));
    }

}
