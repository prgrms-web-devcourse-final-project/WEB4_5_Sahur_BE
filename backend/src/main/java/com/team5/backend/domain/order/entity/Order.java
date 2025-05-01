package com.team5.backend.domain.order.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.team5.backend.domain.groupBuy.entity.GroupBuy;
import com.team5.backend.domain.member.member.entity.Member;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

	@CreationTimestamp
	@Column(nullable = false)
	private LocalDateTime createdAt;

	public static Order create(Member member, GroupBuy groupBuy, Integer quantity) {
		int totalPrice = groupBuy.getProduct().getPrice() * quantity;
		return Order.builder()
			.member(member)
			.groupBuy(groupBuy)
			.totalPrice(totalPrice)
			.status(OrderStatus.BEFOREPAID)
			.quantity(quantity)
			.build();
	}

	public void updateQuantityAndPrice(int quantity, int totalPrice) {
		this.quantity = quantity;
		this.totalPrice = totalPrice;
	}

	public void markAsPaid() {
		if (this.status != OrderStatus.BEFOREPAID) {
			throw new IllegalStateException("결제는 WAITING 상태에서만 진행할 수 있습니다.");
		}
		this.status = OrderStatus.PAID;
	}

	public void cancel() {
		this.status = OrderStatus.CANCELED;
	}
}
