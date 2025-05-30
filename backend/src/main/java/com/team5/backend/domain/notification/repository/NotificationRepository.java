package com.team5.backend.domain.notification.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.team5.backend.domain.notification.entity.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    Page<Notification> findByMemberMemberId(Long memberId, Pageable pageable);

    // 안 읽은 알림 개수 조회
    long countByMemberMemberIdAndIsReadFalse(Long memberId);
}
