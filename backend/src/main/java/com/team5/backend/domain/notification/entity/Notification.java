package com.team5.backend.domain.notification.entity;

import com.team5.backend.domain.member.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)

public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long notificationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memberId", nullable = false)
    private Member member;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;

    @Column(length = 255, nullable = false)
    private String title = "";

    @Column(length = 255)
    private String message = "";

    @Column(columnDefinition = "TEXT")
    private String url = "";

    private Boolean read = false;

    @CreatedDate
    private LocalDateTime createdAt;
}
