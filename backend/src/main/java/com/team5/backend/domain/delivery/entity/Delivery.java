package com.team5.backend.domain.delivery.entity;

import com.team5.backend.domain.order.entity.Order;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Delivery {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long deliveryId;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "orderId", nullable = false)
	private Order order;

	@Column(nullable = false, length = 255)
	private String address;

	private Integer pccc;

	@Column(nullable = false)
	private Integer contact;

	private Delivery(Order order, String address, Integer pccc, Integer contact) {
		this.order = order;
		this.address = address;
		this.pccc = pccc;
		this.contact = contact;
	}

	public static Delivery create(Order order, String address, Integer pccc, Integer contact) {
		return new Delivery(order, address, pccc, contact);
	}

	public void updateDeliveryInfo(String address, Integer contact, Integer pccc) {
		this.address = address;
		this.contact = contact;
		this.pccc = pccc;
	}
}
