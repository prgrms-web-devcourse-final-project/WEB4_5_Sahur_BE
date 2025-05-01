package com.team5.backend.domain.notification.repository;

import com.team5.backend.domain.notification.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    Page<Notification> findByMemberMemberId(Long memberId, Pageable pageable);
}
