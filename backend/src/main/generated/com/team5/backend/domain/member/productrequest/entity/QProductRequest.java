package com.team5.backend.domain.member.productrequest.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QProductRequest is a Querydsl query type for ProductRequest
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QProductRequest extends EntityPathBase<ProductRequest> {

    private static final long serialVersionUID = 626044612L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QProductRequest productRequest = new QProductRequest("productRequest");

    public final com.team5.backend.domain.category.entity.QCategory category;

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final StringPath description = createString("description");

    public final ListPath<String, StringPath> imageUrls = this.<String, StringPath>createList("imageUrls", String.class, StringPath.class, PathInits.DIRECT2);

    public final com.team5.backend.domain.member.member.entity.QMember member;

    public final NumberPath<Long> productRequestId = createNumber("productRequestId", Long.class);

    public final StringPath productUrl = createString("productUrl");

    public final EnumPath<ProductRequestStatus> status = createEnum("status", ProductRequestStatus.class);

    public final StringPath title = createString("title");

    public QProductRequest(String variable) {
        this(ProductRequest.class, forVariable(variable), INITS);
    }

    public QProductRequest(Path<? extends ProductRequest> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QProductRequest(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QProductRequest(PathMetadata metadata, PathInits inits) {
        this(ProductRequest.class, metadata, inits);
    }

    public QProductRequest(Class<? extends ProductRequest> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.category = inits.isInitialized("category") ? new com.team5.backend.domain.category.entity.QCategory(forProperty("category")) : null;
        this.member = inits.isInitialized("member") ? new com.team5.backend.domain.member.member.entity.QMember(forProperty("member"), inits.get("member")) : null;
    }

}

