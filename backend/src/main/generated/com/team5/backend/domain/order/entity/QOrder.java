package com.team5.backend.domain.order.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QOrder is a Querydsl query type for Order
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QOrder extends EntityPathBase<Order> {

    private static final long serialVersionUID = -342555600L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QOrder order = new QOrder("order1");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final com.team5.backend.domain.delivery.entity.QDelivery delivery;

    public final com.team5.backend.domain.groupBuy.entity.QGroupBuy groupBuy;

    public final com.team5.backend.domain.member.member.entity.QMember member;

    public final NumberPath<Long> orderId = createNumber("orderId", Long.class);

    public final com.team5.backend.domain.product.entity.QProduct product;

    public final NumberPath<Integer> quantity = createNumber("quantity", Integer.class);

    public final EnumPath<OrderStatus> status = createEnum("status", OrderStatus.class);

    public final NumberPath<Integer> totalPrice = createNumber("totalPrice", Integer.class);

    public QOrder(String variable) {
        this(Order.class, forVariable(variable), INITS);
    }

    public QOrder(Path<? extends Order> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QOrder(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QOrder(PathMetadata metadata, PathInits inits) {
        this(Order.class, metadata, inits);
    }

    public QOrder(Class<? extends Order> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.delivery = inits.isInitialized("delivery") ? new com.team5.backend.domain.delivery.entity.QDelivery(forProperty("delivery"), inits.get("delivery")) : null;
        this.groupBuy = inits.isInitialized("groupBuy") ? new com.team5.backend.domain.groupBuy.entity.QGroupBuy(forProperty("groupBuy"), inits.get("groupBuy")) : null;
        this.member = inits.isInitialized("member") ? new com.team5.backend.domain.member.member.entity.QMember(forProperty("member"), inits.get("member")) : null;
        this.product = inits.isInitialized("product") ? new com.team5.backend.domain.product.entity.QProduct(forProperty("product"), inits.get("product")) : null;
    }

}

