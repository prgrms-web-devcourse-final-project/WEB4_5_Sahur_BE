package com.team5.backend.domain.delivery.entity;

import com.team5.backend.domain.delivery.dto.DeliveryReqDto;
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

	public static Delivery create(Order order, DeliveryReqDto request) {
		return new Delivery(
				order,
				request.getAddress(),
				request.getPccc(),
				request.getContact(),
				request.getStatus(),
				request.getShipping()
		);
	}

	public void updateDeliveryInfo(DeliveryReqDto request) {
		this.address = request.getAddress();
		this.pccc = request.getPccc();
		this.contact = request.getContact();
		this.status = request.getStatus();
		this.shipping = request.getShipping();
	}
}
