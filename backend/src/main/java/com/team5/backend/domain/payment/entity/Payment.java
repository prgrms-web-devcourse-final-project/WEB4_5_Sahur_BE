package com.team5.backend.domain.payment.entity;

import com.team5.backend.domain.order.entity.Order;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Payment {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long paymentId;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "orderId", nullable = false)
	private Order order;

	@Column(nullable = false)
	private String paymentKey;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private PaymentStatus status;

	private Payment(Order order, String paymentKey) {
		this.order = order;
		this.paymentKey = paymentKey;
		this.status = PaymentStatus.REQUESTED;
	}

	public static Payment create(Order order, String paymentKey) {
		return new Payment(order, paymentKey);
	}

	public void confirm() {
		this.status = PaymentStatus.CONFIRMED;
	}

	public void cancel() {
		this.status = PaymentStatus.CANCELED;
	}
}
