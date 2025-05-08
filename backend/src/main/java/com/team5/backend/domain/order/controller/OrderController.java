package com.team5.backend.domain.order.controller;

import com.team5.backend.domain.member.member.service.AuthService;
import com.team5.backend.domain.order.dto.*;
import com.team5.backend.domain.order.entity.Order;
import com.team5.backend.domain.order.entity.OrderStatus;
import com.team5.backend.domain.order.service.OrderService;
import com.team5.backend.global.dto.Empty;
import com.team5.backend.global.dto.RsData;
import com.team5.backend.global.exception.RsDataUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

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
    public RsData<OrderCreateResDto> createOrder(@RequestBody OrderCreateReqDto request) {
        Order order = orderService.createOrder(request);
        return RsDataUtil.success("주문이 성공적으로 생성되었습니다.", OrderCreateResDto.from(order));
    }

    @Operation(summary = "주문 목록 조회", description = "모든 주문 목록을 조회하거나 주문번호, 상태로 필터링할 수 있습니다.")
    @GetMapping
    public RsData<Page<OrderListResDto>> getOrders(
            @RequestParam(required = false) Long orderId,
            @RequestParam(required = false) OrderStatus status,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<OrderListResDto> dtoPage = orderService.getOrders(orderId, status, pageable);
        return RsDataUtil.success("주문 목록 조회에 성공했습니다.", dtoPage);
    }

    @Operation(summary = "내 주문 조회", description = "로그인한 회원의 주문 목록을 조회합니다. 상태 필터링할 수 있습니다.")
    @GetMapping("/me")
    public RsData<Page<OrderListResDto>> getMemberOrders(
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestParam(required = false) String status,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Long memberId = authService.getLoggedInMember(token).getMemberId();
        Page<OrderListResDto> dtoPage = orderService.getOrdersByMember(memberId, status, pageable);
        return RsDataUtil.success("회원 주문 목록 조회에 성공했습니다.", dtoPage);
    }

    @Operation(summary = "주문 상세 조회", description = "주문 ID를 통해 상세 정보를 조회합니다.")
    @GetMapping("/{orderId}")
    public RsData<OrderDetailResDto> getOrderDetail(@PathVariable Long orderId) {
        OrderDetailResDto response = orderService.getOrderDetail(orderId);
        return RsDataUtil.success("주문 상세 조회에 성공했습니다.", response);
    }

    @Operation(summary = "주문 수정", description = "수량을 수정하면 총 가격도 변경됩니다.")
    @PatchMapping("/{orderId}")
    public RsData<OrderUpdateResDto> updateOrder(
            @PathVariable Long orderId,
            @RequestBody OrderUpdateReqDto request
    ) {
        Order order = orderService.updateOrder(orderId, request);
        return RsDataUtil.success("주문 정보가 수정되었습니다.", OrderUpdateResDto.from(order));
    }

    @Operation(summary = "주문 취소", description = "주문을 취소합니다.")
    @DeleteMapping("/{orderId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public RsData<Empty> cancelOrder(@PathVariable Long orderId) {
        orderService.cancelOrder(orderId);
        return RsDataUtil.success("주문이 성공적으로 취소되었습니다.");
    }
}
