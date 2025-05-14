package com.team5.backend.domain.history.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QHistory is a Querydsl query type for History
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QHistory extends EntityPathBase<History> {

    private static final long serialVersionUID = 2611120L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QHistory history = new QHistory("history");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final com.team5.backend.domain.groupBuy.entity.QGroupBuy groupBuy;

    public final NumberPath<Long> historyId = createNumber("historyId", Long.class);

    public final com.team5.backend.domain.member.member.entity.QMember member;

    public final com.team5.backend.domain.order.entity.QOrder order;

    public final com.team5.backend.domain.product.entity.QProduct product;

    public final BooleanPath writable = createBoolean("writable");

    public QHistory(String variable) {
        this(History.class, forVariable(variable), INITS);
    }

    public QHistory(Path<? extends History> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QHistory(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QHistory(PathMetadata metadata, PathInits inits) {
        this(History.class, metadata, inits);
    }

    public QHistory(Class<? extends History> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.groupBuy = inits.isInitialized("groupBuy") ? new com.team5.backend.domain.groupBuy.entity.QGroupBuy(forProperty("groupBuy"), inits.get("groupBuy")) : null;
        this.member = inits.isInitialized("member") ? new com.team5.backend.domain.member.member.entity.QMember(forProperty("member"), inits.get("member")) : null;
        this.order = inits.isInitialized("order") ? new com.team5.backend.domain.order.entity.QOrder(forProperty("order"), inits.get("order")) : null;
        this.product = inits.isInitialized("product") ? new com.team5.backend.domain.product.entity.QProduct(forProperty("product"), inits.get("product")) : null;
    }

}

