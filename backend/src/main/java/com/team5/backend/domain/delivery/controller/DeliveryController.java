package com.team5.backend.domain.delivery.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import com.team5.backend.domain.delivery.dto.DeliveryReqDto;
import com.team5.backend.domain.delivery.dto.DeliveryResDto;
import com.team5.backend.domain.delivery.entity.Delivery;
import com.team5.backend.domain.delivery.service.DeliveryService;
import com.team5.backend.global.dto.Empty;
import com.team5.backend.global.dto.RsData;
import com.team5.backend.global.exception.RsDataUtil;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Delivery", description = "배송 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/deliveries")
public class DeliveryController {

	private final DeliveryService deliveryService;

	@Operation(summary = "주문별 배송 정보 등록", description = "해당 주문에 대한 배송 정보를 등록합니다.")
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
	@GetMapping
	public RsData<Page<DeliveryResDto>> getAllDeliveries(
		@Parameter(description = "페이지 정보") @PageableDefault(size = 10) Pageable pageable
	) {
		Page<DeliveryResDto> dtoPage = deliveryService.getAllDeliveries(pageable).map(DeliveryResDto::fromEntity);
		return RsDataUtil.success("배송 전체 조회 성공", dtoPage);
	}

	@Operation(summary = "배송 정보 수정", description = "배송 정보를 수정합니다.")
	@PatchMapping("/{deliveryId}")
	public RsData<DeliveryResDto> updateDelivery(
		@Parameter(description = "배송 ID") @PathVariable Long deliveryId,
		@RequestBody @Valid DeliveryReqDto request
	) {
		Delivery delivery = deliveryService.updateDelivery(deliveryId, request);
		DeliveryResDto response = DeliveryResDto.fromEntity(delivery);
		return RsDataUtil.success("배송 정보 수정 성공", response);
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
