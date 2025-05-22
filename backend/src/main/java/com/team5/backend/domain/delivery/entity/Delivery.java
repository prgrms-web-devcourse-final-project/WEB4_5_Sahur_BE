package com.team5.backend.domain.delivery.entity;

import com.team5.backend.domain.delivery.dto.DeliveryReqDto;
import com.team5.backend.domain.order.entity.Order;
import com.team5.backend.global.entity.Address;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;

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

    @Embedded
    private Address address;

    private Integer pccc;

    @Column(nullable = false, length = 20)
    private String contact;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeliveryStatus status;

    @Column(nullable = false)
    private String shipping;

    private Delivery(Order order, Address address, Integer pccc, String contact, DeliveryStatus status, String shipping) {
        this.order = order;
        this.address = address;
        this.pccc = pccc;
        this.contact = contact;
        this.status = status;
        this.shipping = shipping;
    }

    public static Delivery create(Order order, String shipping, DeliveryReqDto request) {
        Delivery delivery = new Delivery(
                order,
                request.toAddress(),
                request.getPccc(),
                request.getContact(),
                DeliveryStatus.PREPARING,
                shipping
        );
        order.setDelivery(delivery);
        return delivery;
    }

    public void updateDeliveryInfo(DeliveryReqDto request) {
        this.address = request.toAddress();
        this.pccc = request.getPccc();
        this.contact = request.getContact();
    }

    public void updateDeliveryStatus(DeliveryStatus status) {
        this.status = status;
    }

    public void setOrder(Order order) {
        this.order = order;
        order.setDelivery(this);
    }

}
