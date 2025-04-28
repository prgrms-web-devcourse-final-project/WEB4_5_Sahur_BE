package com.team5.backend.domain.order.entity;

import com.team5.backend.domain.delivery.entity.Delivery;
import com.team5.backend.domain.groupBuy.entity.GroupBuy;
import com.team5.backend.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "orders")
public class Order {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long orderId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "memberId", nullable = false)
	private Member member;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "groupId", nullable = false)
	private GroupBuy groupBuy;

	@Column(nullable = false)
	private Integer totalPrice;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private OrderStatus status;

	@Column(nullable = false)
	private Integer quantity;

	@Column
	private Integer shipping;

	@Column(nullable = false)
	private LocalDateTime createdAt;

	@OneToOne(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private Delivery delivery;

	public static Order create(Member member, GroupBuy groupBuy, Integer totalPrice, Integer quantity) {
		return Order.builder()
			.member(member)
			.groupBuy(groupBuy)
			.totalPrice(totalPrice)
			.status(OrderStatus.WAITING)
			.quantity(quantity)
			.createdAt(LocalDateTime.now())
			.build();
	}

	public void updateQuantityAndPrice(int quantity, int totalPrice) {
		this.quantity = quantity;
		this.totalPrice = totalPrice;
	}

	public void cancel() {
		this.status = OrderStatus.CANCELED;
	}
}
