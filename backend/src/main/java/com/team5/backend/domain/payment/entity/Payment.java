package com.team5.backend.domain.payment.entity;

import com.team5.backend.domain.order.entity.Order;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long paymentId;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "orderId", nullable = false)
	private Order order;

	@Column(nullable = false)
	private String paymentKey;

	private Payment(Order order, String paymentKey) {
		this.order = order;
		this.paymentKey = paymentKey;
	}

	public static Payment create(Order order, String paymentKey) {
		return new Payment(order, paymentKey);
	}
}
