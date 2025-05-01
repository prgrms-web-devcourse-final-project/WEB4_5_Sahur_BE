package com.team5.backend.domain.delivery.entity;

import com.team5.backend.domain.order.entity.Order;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
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

	@Column(nullable = false, length = 20)
	private String contact;

	private Delivery(Order order, String address, String contact, Integer pccc) {
		this.order = order;
		this.address = address;
		this.contact = contact;
		this.pccc = pccc;
	}

	public static Delivery create(Order order, String address, String contact, Integer pccc) {
		return new Delivery(order, address, contact, pccc);
	}

	public void updateDeliveryInfo(String address, String contact, Integer pccc) {
		this.address = address;
		this.contact = contact;
		this.pccc = pccc;
	}

	public void updateAddressAndContact(String address, String contact) {
		this.address = address;
		this.contact = contact;
	}
}
