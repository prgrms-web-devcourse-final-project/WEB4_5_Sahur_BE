package com.team5.backend.domain.notification.controller;

import com.team5.backend.domain.notification.dto.NotificationCreateReqDto;
import com.team5.backend.domain.notification.dto.NotificationResDto;
import com.team5.backend.domain.notification.dto.NotificationUpdateReqDto;
import com.team5.backend.domain.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping
    public ResponseEntity<NotificationResDto> createNotification(@RequestBody NotificationCreateReqDto request) {
        NotificationResDto response = notificationService.createNotification(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<NotificationResDto>> getAllNotifications(
            @PageableDefault(size = 5) Pageable pageable
    ) {
        Page<NotificationResDto> response = notificationService.getAllNotifications(pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<NotificationResDto> getNotificationById(@PathVariable Long id) {
        return notificationService.getNotificationById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new RuntimeException("Notification not found with id " + id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<NotificationResDto> updateNotification(
            @PathVariable Long id,
            @RequestBody NotificationUpdateReqDto request
    ) {
        NotificationResDto response = notificationService.updateNotification(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotification(@PathVariable Long id) {
        notificationService.deleteNotification(id);
        return ResponseEntity.noContent().build();
    }
}
