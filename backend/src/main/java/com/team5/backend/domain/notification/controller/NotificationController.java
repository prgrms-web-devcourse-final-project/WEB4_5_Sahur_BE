package com.team5.backend.domain.notification.controller;

import com.team5.backend.domain.notification.dto.NotificationCreateReqDto;
import com.team5.backend.domain.notification.dto.NotificationResDto;
import com.team5.backend.domain.notification.dto.NotificationUpdateReqDto;
import com.team5.backend.domain.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping
    public NotificationResDto createNotification(@RequestBody NotificationCreateReqDto request) {
        return notificationService.createNotification(request);
    }

    @GetMapping
    public List<NotificationResDto> getAllNotifications() {
        return notificationService.getAllNotifications();
    }

    @GetMapping("/{id}")
    public NotificationResDto getNotificationById(@PathVariable Long id) {
        return notificationService.getNotificationById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found with id " + id));
    }

    @PutMapping("/{id}")
    public NotificationResDto updateNotification(@PathVariable Long id, @RequestBody NotificationUpdateReqDto request) {
        return notificationService.updateNotification(id, request);
    }

    @DeleteMapping("/{id}")
    public void deleteNotification(@PathVariable Long id) {
        notificationService.deleteNotification(id);
    }
}
