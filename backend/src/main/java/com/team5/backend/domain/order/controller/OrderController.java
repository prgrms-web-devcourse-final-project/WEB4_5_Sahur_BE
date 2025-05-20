package com.team5.backend.domain.order.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.team5.backend.domain.member.member.service.AuthService;
import com.team5.backend.domain.order.dto.OrderCreateReqDto;
import com.team5.backend.domain.order.dto.OrderCreateResDto;
import com.team5.backend.domain.order.dto.OrderDetailResDto;
import com.team5.backend.domain.order.dto.OrderListResDto;
import com.team5.backend.domain.order.dto.OrderPaymentInfoResDto;
import com.team5.backend.domain.order.dto.OrderUpdateReqDto;
import com.team5.backend.domain.order.dto.OrderUpdateResDto;
import com.team5.backend.domain.order.entity.FilterStatus;
import com.team5.backend.domain.order.entity.Order;
import com.team5.backend.domain.order.service.OrderService;
import com.team5.backend.global.dto.Empty;
import com.team5.backend.global.dto.RsData;
import com.team5.backend.global.exception.RsDataUtil;
import com.team5.backend.global.security.PrincipalDetails;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;

import jakarta.validation.Valid;

@Tag(name = "Order", description = "주문 관련 API")
@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final AuthService authService;

    @Operation(summary = "주문 생성", description = "회원이 상품을 주문합니다.")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RsData<OrderCreateResDto> createOrder(
            @AuthenticationPrincipal PrincipalDetails userDetails,
            @RequestBody @Valid OrderCreateReqDto request
    ) {
        OrderCreateResDto dto = orderService.createOrder(request, userDetails);
        return RsDataUtil.success("주문이 성공적으로 생성되었습니다.", dto);
    }

    @Operation(summary = "주문 목록 조회", description = "모든 주문 목록을 조회하거나 주문번호, 상태로 필터링할 수 있습니다.")
    @GetMapping
    public RsData<Page<OrderListResDto>> getOrders(
            @RequestParam(required = false) Long orderId,
            @RequestParam(name = "status", required = false) String status,
            @PageableDefault(size = 5, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<OrderListResDto> dtoPage = orderService.getOrders(orderId, status, pageable);
        return RsDataUtil.success("주문 목록 조회에 성공했습니다.", dtoPage);
    }

    @Operation(summary = "내 주문 조회", description = "로그인한 회원의 주문 목록을 조회합니다. 상태 필터링할 수 있습니다.")
    @GetMapping("/me")
    public RsData<Page<OrderListResDto>> getMemberOrders(
            @AuthenticationPrincipal PrincipalDetails userDetails,
            @RequestParam(name = "status", required = false) FilterStatus status,
            @PageableDefault(size = 5) Pageable pageable
    ) {
        Long memberId = userDetails.getMember().getMemberId();
        Page<OrderListResDto> dtoPage = orderService.getOrdersByMember(memberId, status, pageable);
        return RsDataUtil.success("회원 주문 목록 조회에 성공했습니다.", dtoPage);
    }

    @Operation(summary = "이번 달 총 매출 조회", description = "주문 상태가 PAID (결제 완료)인 주문들에 대한 매출 총 합")
    @GetMapping("/monthly-sales")
    public RsData<Long> getMonthlySales() {
        Long totalSales = orderService.getMonthlyCompletedSales();
        return RsDataUtil.success("이번 달 총 매출 조회 성공", totalSales);
    }

    @Operation(summary = "주문 상세 조회", description = "주문 ID를 통해 상세 정보를 조회합니다.")
    @GetMapping("/{orderId}")
    public RsData<OrderDetailResDto> getOrderDetail(
            @PathVariable Long orderId
    ) {
        OrderDetailResDto response = orderService.getOrderDetail(orderId);
        return RsDataUtil.success("주문 상세 조회에 성공했습니다.", response);
    }

    @Operation(summary = "주문 수정", description = "수량을 수정하면 총 가격도 변경됩니다.")
    @PatchMapping("/{orderId}")
    public RsData<OrderUpdateResDto> updateOrder(
            @PathVariable Long orderId,
            @RequestBody @Valid OrderUpdateReqDto request
    ) {
        Order order = orderService.updateOrder(orderId, request);
        return RsDataUtil.success("주문 정보가 수정되었습니다.", OrderUpdateResDto.from(order));
    }

    @Operation(summary = "주문 취소", description = "주문을 취소합니다.")
    @DeleteMapping("/{orderId}")
    public RsData<Empty> cancelOrder(
            @PathVariable Long orderId
    ) {
        orderService.cancelOrder(orderId);
        return RsDataUtil.success("주문이 성공적으로 취소되었습니다.");
    }

    @Operation(summary = "결제용 주문 정보 조회", description = "해당 주문 ID의 결제에 필요한 주문 정보를 반환합니다.")
    @GetMapping("/{orderId}/payment")
    public RsData<OrderPaymentInfoResDto> getOrderPaymentInfo(
            @PathVariable Long orderId
    ) {
        OrderPaymentInfoResDto response = orderService.getOrderPaymentInfo(orderId);
        return RsDataUtil.success("결제용 주문 정보 조회에 성공했습니다.", response);
    }
}
