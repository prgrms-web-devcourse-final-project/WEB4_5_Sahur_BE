package com.team5.backend.domain.delivery.controller;

import com.team5.backend.domain.delivery.dto.DeliveryReqDto;
import com.team5.backend.domain.delivery.dto.DeliveryResDto;
import com.team5.backend.domain.delivery.dto.DeliveryStatusUpdateReqDto;
import com.team5.backend.domain.delivery.dto.DeliveryStatusUpdateResDto;
import com.team5.backend.domain.delivery.entity.Delivery;
import com.team5.backend.domain.delivery.entity.DeliveryStatus;
import com.team5.backend.domain.delivery.service.DeliveryService;
import com.team5.backend.global.annotation.CheckAdmin;
import com.team5.backend.global.dto.Empty;
import com.team5.backend.global.dto.RsData;
import com.team5.backend.global.exception.RsDataUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Delivery", description = "배송 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/deliveries")
public class DeliveryController {

    private final DeliveryService deliveryService;

    @Operation(summary = "주문별 배송 정보 등록", description = "해당 주문에 대한 배송 정보를 등록합니다.")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/order/{orderId}")
    public RsData<DeliveryResDto> createDelivery(
            @Parameter(description = "주문 ID") @PathVariable Long orderId,
            @RequestBody @Valid DeliveryReqDto request
    ) {
        Delivery delivery = deliveryService.createDelivery(orderId, request);
        DeliveryResDto response = DeliveryResDto.fromEntity(delivery);
        return RsDataUtil.success("주문별 배송 정보 등록 성공", response);
    }

    @Operation(summary = "주문별 배송 정보 조회", description = "주문에 해당되는 배송 정보를 조회합니다.")
    @GetMapping("/order/{orderId}")
    public RsData<DeliveryResDto> getDeliveryByOrder(
            @Parameter(description = "주문 ID") @PathVariable Long orderId
    ) {
        Delivery delivery = deliveryService.getDeliveryByOrder(orderId);
        DeliveryResDto response = DeliveryResDto.fromEntity(delivery);
        return RsDataUtil.success("주문별 배송 정보 조회 성공", response);
    }

    @Operation(summary = "전체 배송 조회", description = "전체 배송 정보 목록을 조회합니다.")
    @CheckAdmin
    @GetMapping
    public RsData<Page<DeliveryResDto>> getAllDeliveries(
            @ParameterObject
            @Parameter(description = "페이지 정보") @PageableDefault(size = 10) Pageable pageable
    ) {
        Page<DeliveryResDto> dtoPage = deliveryService.getAllDeliveries(pageable).map(DeliveryResDto::fromEntity);
        return RsDataUtil.success("배송 전체 조회 성공", dtoPage);
    }

    @Operation(summary = "배송 상태별 총 개수 조회", description = "기본 상태가 INDELIVERY (배송중)인 상품의 개수 반환")
    @CheckAdmin
    @GetMapping("/count")
    public RsData<Long> getDeliveryCountByStatus(
            @RequestParam(required = false, defaultValue = "INDELIVERY") DeliveryStatus status
    ) {
        long count = deliveryService.countDeliveriesByStatus(status);
        return RsDataUtil.success("배송 상태별 개수 조회 성공", count);
    }

    @Operation(summary = "배송 정보 수정", description = "배송 정보 전체를 수정합니다.")
    @PatchMapping("/{orderId}")
    public RsData<DeliveryResDto> updateDelivery(
            @Parameter(description = "주문 ID") @PathVariable Long orderId,
            @RequestBody @Valid DeliveryReqDto request,
            @RequestParam(name = "status") DeliveryStatus status
    ) {
        Delivery delivery = deliveryService.updateDeliveryInfo(orderId, request, status);
        DeliveryResDto response = DeliveryResDto.fromEntity(delivery);
        return RsDataUtil.success("배송 정보 수정 성공", response);
    }

    @Operation(summary = "배송 상태 일괄 변경", description = "여러 배송 상태를 일괄 수정 (관리자)")
    @CheckAdmin
    @PatchMapping("/batch")
    public RsData<List<DeliveryStatusUpdateResDto>> updateDeliveryStatuses(
            @RequestBody @Valid DeliveryStatusUpdateReqDto request
    ) {
        List<DeliveryStatusUpdateResDto> result = deliveryService.updateDeliveryStatuses(request);
        return RsDataUtil.success("배송 상태 일괄 변경 완료", result);
    }

    @Operation(summary = "배송 정보 삭제", description = "배송 정보를 삭제합니다.")
    @DeleteMapping("/{deliveryId}")
    public RsData<Empty> deleteDelivery(
            @Parameter(description = "배송 ID") @PathVariable Long deliveryId
    ) {
        deliveryService.deleteDelivery(deliveryId);
        return RsDataUtil.success("배송 정보 삭제 성공");
    }

}
