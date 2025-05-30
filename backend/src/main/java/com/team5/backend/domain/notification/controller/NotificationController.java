package com.team5.backend.domain.notification.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.team5.backend.domain.notification.dto.NotificationCreateReqDto;
import com.team5.backend.domain.notification.dto.NotificationResDto;
import com.team5.backend.domain.notification.dto.NotificationUpdateReqDto;
import com.team5.backend.domain.notification.service.NotificationService;
import com.team5.backend.global.annotation.CheckAdmin;
import com.team5.backend.global.dto.Empty;
import com.team5.backend.global.dto.RsData;
import com.team5.backend.global.exception.RsDataUtil;
import com.team5.backend.global.security.PrincipalDetails;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;

import jakarta.validation.Valid;

@Tag(name = "Notification", description = "알림 API")
@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @Operation(summary = "알림 생성", description = "새로운 알림을 생성합니다.")
    @CheckAdmin
    @PostMapping
    public RsData<NotificationResDto> createNotification(
            @AuthenticationPrincipal PrincipalDetails userDetails,
            @Valid @RequestBody NotificationCreateReqDto request
    ) {
        NotificationResDto response = notificationService.createNotification(request, userDetails);
        return RsDataUtil.success("알림 생성 성공", response);
    }

    @Operation(summary = "전체 알림 조회", description = "모든 알림을 최신순으로 조회합니다.")
    @CheckAdmin
    @GetMapping
    public RsData<Page<NotificationResDto>> getAllNotifications(
            @PageableDefault(size = 5) Pageable pageable
    ) {
        Page<NotificationResDto> response = notificationService.getAllNotifications(pageable);
        return RsDataUtil.success("알림 목록 조회 성공", response);
    }

    @Operation(summary = "단건 알림 조회", description = "알림 ID로 특정 알림을 조회합니다.")
    @CheckAdmin
    @GetMapping("/{id}")
    public RsData<NotificationResDto> getNotificationById(
            @Parameter(description = "알림 ID") @PathVariable Long id
    ) {
        NotificationResDto response = notificationService.getNotificationById(id);
        return RsDataUtil.success("알림 조회 성공", response);
    }

    @Operation(summary = "알림 수정 (전체)", description = "알림을 수정합니다.")
    @CheckAdmin
    @PutMapping("/{id}")
    public RsData<NotificationResDto> updateNotification(
            @PathVariable Long id,
            @Valid @RequestBody NotificationUpdateReqDto request
    ) {
        NotificationResDto response = notificationService.updateNotification(id, request);
        return RsDataUtil.success("알림 수정 성공", response);
    }

    @Operation(summary = "알림 읽음 처리", description = "알림의 읽음 상태를 부분 수정합니다.")
    @PatchMapping("/{id}")
    public RsData<NotificationResDto> patchNotification(@PathVariable Long id) {
        NotificationResDto response = notificationService.patchNotification(id);
        return RsDataUtil.success("알림 읽음 처리 성공", response);
    }

    @Operation(summary = "알림 삭제", description = "알림을 삭제합니다.")
    @CheckAdmin
    @DeleteMapping("/{id}")
    public RsData<Empty> deleteNotification(@PathVariable Long id) {
        notificationService.deleteNotification(id);
        return RsDataUtil.success("알림 삭제 성공");
    }

    @Operation(summary = "내 알림 목록 조회", description = "접속 중인 회원의 알림 목록을 최신순으로 조회합니다.")
    @GetMapping("/member/list")
    public RsData<Page<NotificationResDto>> getMyNotifications(
            @AuthenticationPrincipal PrincipalDetails userDetails,
            @PageableDefault(size = 5) Pageable pageable
    ) {
        Page<NotificationResDto> response = notificationService.getNotificationsByMember(userDetails, pageable);
        return RsDataUtil.success("회원 알림 목록 조회 성공", response);
    }

    @Operation(summary = "공동 구매 강제 종료 알림 (관리자)", description = "관리자 페이지 내부에서 공동 구매를 직접 종료시 알림 생성")
    @PostMapping("/groupBuy/close/{groupBuyId}")
    public RsData<Empty> groupBuyCloseNotifications(
            @PathVariable Long groupBuyId,
            @RequestBody String message
    ) {
        notificationService.groupBuyCloseNotifications(groupBuyId, message);
        return RsDataUtil.success("공동 구매 알림 일괄 생성 성공");
    }

    @Operation(summary = "관심 상품 재오픈 알림", description = "관심 상품으로 설정한 상품이 재오픈시 알림 생성")
    @PostMapping("/dibs/reopen/{groupBuyId}")
    public RsData<Empty> dibsReopenNotifications(
            @PathVariable Long groupBuyId
    ) {
        notificationService.dibsReopenNotifications(groupBuyId);
        return RsDataUtil.success("관심 상품 재오픈 알림 일괄 생성 성공");
    }

}
