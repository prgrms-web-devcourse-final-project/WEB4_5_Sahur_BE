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

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private DeliveryStatus status;

	@Column(nullable = false)
	private String shipping;

	private Delivery(Order order, String address, Integer pccc, String contact, DeliveryStatus status, String shipping) {
		this.order = order;
		this.address = address;
		this.pccc = pccc;
		this.contact = contact;
		this.status = status;
		this.shipping = shipping;
	}

	public static Delivery create(Order order, String address, String contact, Integer pccc, DeliveryStatus status, String shipping) {
		return new Delivery(order, address, pccc, contact, status, shipping);
	}

	public void updateDeliveryInfo(String address, Integer pccc, String contact, DeliveryStatus status, String shipping) {
		this.address = address;
		this.pccc = pccc;
		this.contact = contact;
		this.status = status;
		this.shipping = shipping;
	}

	public void updateAddressAndContact(String address, String contact) {
		this.address = address;
		this.contact = contact;
	}
}
