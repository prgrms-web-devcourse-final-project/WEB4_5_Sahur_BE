package com.team5.backend.domain.review.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QReview is a Querydsl query type for Review
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QReview extends EntityPathBase<Review> {

    private static final long serialVersionUID = 811929786L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QReview review = new QReview("review");

    public final StringPath comment = createString("comment");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final com.team5.backend.domain.history.entity.QHistory history;

    public final ListPath<String, StringPath> imageUrl = this.<String, StringPath>createList("imageUrl", String.class, StringPath.class, PathInits.DIRECT2);

    public final com.team5.backend.domain.member.member.entity.QMember member;

    public final com.team5.backend.domain.product.entity.QProduct product;

    public final NumberPath<Integer> rate = createNumber("rate", Integer.class);

    public final NumberPath<Long> reviewId = createNumber("reviewId", Long.class);

    public QReview(String variable) {
        this(Review.class, forVariable(variable), INITS);
    }

    public QReview(Path<? extends Review> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QReview(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QReview(PathMetadata metadata, PathInits inits) {
        this(Review.class, metadata, inits);
    }

    public QReview(Class<? extends Review> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.history = inits.isInitialized("history") ? new com.team5.backend.domain.history.entity.QHistory(forProperty("history"), inits.get("history")) : null;
        this.member = inits.isInitialized("member") ? new com.team5.backend.domain.member.member.entity.QMember(forProperty("member"), inits.get("member")) : null;
        this.product = inits.isInitialized("product") ? new com.team5.backend.domain.product.entity.QProduct(forProperty("product"), inits.get("product")) : null;
    }

}

