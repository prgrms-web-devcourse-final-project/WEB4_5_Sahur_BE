package com.team5.backend.domain.product.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team5.backend.domain.category.entity.QCategory;
import com.team5.backend.domain.product.entity.Product;
import com.team5.backend.domain.product.entity.QProduct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepositoryCustom {
    private final JPAQueryFactory queryFactory;
    private final QProduct product = QProduct.product;

    @Override
    public Page<Product> findAllByFilter(String categoryName, String keywordName, Pageable pageable) {
        QCategory category = QCategory.category1;
        BooleanBuilder builder = new BooleanBuilder();

        // 필터 조건
        if (StringUtils.hasText(categoryName)) {
            builder.and(product.category.category
                    .stringValue()
                    .equalsIgnoreCase(categoryName.trim()));
        }
        if (StringUtils.hasText(keywordName)) {
            builder.and(product.category.keyword
                    .stringValue()
                    .equalsIgnoreCase(keywordName.trim()));
        }

        // 기본 쿼리
        var query = queryFactory
                .selectFrom(product)
                .join(product.category, category)
                .where(builder);

        // 동적 정렬
        Sort sort = pageable.getSort();
        if (sort.isSorted()) {
            for (Sort.Order order : sort) {
                PathBuilder<Product> path = new PathBuilder<>(Product.class, product.getMetadata());
                OrderSpecifier<?> orderSpec = new OrderSpecifier<>(
                        order.isAscending() ? Order.ASC : Order.DESC,
                        path.getComparable(order.getProperty(), Comparable.class)
                );
                query.orderBy(orderSpec);
            }
        } else {
            query.orderBy(product.createdAt.desc());
        }


        // 페이징 처리
        List<Product> content = query
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 전체 개수
        Long total = queryFactory
                .select(product.count())
                .from(product)
                .join(product.category, category)
                .where(builder)
                .fetchOne();
        long totalCount = total == null ? 0L : total;
        return new PageImpl<>(content, pageable, totalCount);
    }
}
