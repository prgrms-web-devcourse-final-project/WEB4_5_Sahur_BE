package com.team5.backend.domain.delivery.entity;

import java.util.Optional;

public enum DeliveryStatus {
    PREPARING,    // 배송 준비
    INDELIVERY,   // 배송 중
    COMPLETED;    // 배송 완료

    public Optional<DeliveryStatus> next() {
        return switch (this) {
            case PREPARING -> Optional.of(INDELIVERY);
            case INDELIVERY -> Optional.of(COMPLETED);
            default -> Optional.empty();
        };
    }

    public boolean canTransitionTo(DeliveryStatus target) {
        return this.ordinal() < target.ordinal();
    }
}
