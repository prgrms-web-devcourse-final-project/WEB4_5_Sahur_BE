package com.team5.backend.domain.groupBuy.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QGroupBuy is a Querydsl query type for GroupBuy
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QGroupBuy extends EntityPathBase<GroupBuy> {

    private static final long serialVersionUID = -1596951720L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QGroupBuy groupBuy = new QGroupBuy("groupBuy");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final NumberPath<Integer> currentParticipantCount = createNumber("currentParticipantCount", Integer.class);

    public final DateTimePath<java.time.LocalDateTime> deadline = createDateTime("deadline", java.time.LocalDateTime.class);

    public final NumberPath<Long> groupBuyId = createNumber("groupBuyId", Long.class);

    public final com.team5.backend.domain.product.entity.QProduct product;

    public final NumberPath<Integer> round = createNumber("round", Integer.class);

    public final EnumPath<GroupBuyStatus> status = createEnum("status", GroupBuyStatus.class);

    public final NumberPath<Integer> targetParticipants = createNumber("targetParticipants", Integer.class);

    public QGroupBuy(String variable) {
        this(GroupBuy.class, forVariable(variable), INITS);
    }

    public QGroupBuy(Path<? extends GroupBuy> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QGroupBuy(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QGroupBuy(PathMetadata metadata, PathInits inits) {
        this(GroupBuy.class, metadata, inits);
    }

    public QGroupBuy(Class<? extends GroupBuy> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.product = inits.isInitialized("product") ? new com.team5.backend.domain.product.entity.QProduct(forProperty("product"), inits.get("product")) : null;
    }

}

