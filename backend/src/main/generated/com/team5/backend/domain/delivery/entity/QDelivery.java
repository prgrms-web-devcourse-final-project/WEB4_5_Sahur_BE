package com.team5.backend.domain.delivery.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QDelivery is a Querydsl query type for Delivery
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QDelivery extends EntityPathBase<Delivery> {

    private static final long serialVersionUID = 248650290L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QDelivery delivery = new QDelivery("delivery");

    public final com.team5.backend.global.entity.QAddress address;

    public final StringPath contact = createString("contact");

    public final NumberPath<Long> deliveryId = createNumber("deliveryId", Long.class);

    public final com.team5.backend.domain.order.entity.QOrder order;

    public final NumberPath<Integer> pccc = createNumber("pccc", Integer.class);

    public final StringPath shipping = createString("shipping");

    public final EnumPath<DeliveryStatus> status = createEnum("status", DeliveryStatus.class);

    public QDelivery(String variable) {
        this(Delivery.class, forVariable(variable), INITS);
    }

    public QDelivery(Path<? extends Delivery> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QDelivery(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QDelivery(PathMetadata metadata, PathInits inits) {
        this(Delivery.class, metadata, inits);
    }

    public QDelivery(Class<? extends Delivery> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.address = inits.isInitialized("address") ? new com.team5.backend.global.entity.QAddress(forProperty("address")) : null;
        this.order = inits.isInitialized("order") ? new com.team5.backend.domain.order.entity.QOrder(forProperty("order"), inits.get("order")) : null;
    }

}

