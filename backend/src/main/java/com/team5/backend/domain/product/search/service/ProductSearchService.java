//package com.team5.backend.domain.product.search.service;
//
//import co.elastic.clients.elasticsearch.ElasticsearchClient;
//import co.elastic.clients.elasticsearch.core.SearchRequest;
//import co.elastic.clients.elasticsearch.core.SearchResponse;
//import co.elastic.clients.elasticsearch.core.search.Hit;
//import com.team5.backend.domain.product.entity.Product;
//import com.team5.backend.domain.product.search.document.ProductDocument;
//import com.team5.backend.domain.product.search.repository.ProductSearchRepository;
//import com.team5.backend.global.exception.CustomException;
//import com.team5.backend.global.exception.code.ProductSearchErrorCode;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//import java.io.IOException;
//import java.util.List;
//import java.util.Objects;
//
//@Service
//@RequiredArgsConstructor
//public class ProductSearchService {
//
//    private final ElasticsearchClient elasticsearchClient;
//    private final ProductSearchRepository productSearchRepository;
//
//    /**
//     * 상품을 Elasticsearch에 색인합니다.
//     *
//     * @param product 색인할 상품
//     */
//    public void index(Product product) {
//        ProductDocument doc = ProductDocument.builder()
//                .id(product.getProductId().toString())
//                .categoryId(product.getCategory().getCategoryId())
//                .title(product.getTitle())
//                .description(product.getDescription())
//                .imageUrl(product.getImageUrl())
//                .price(product.getPrice())
//                .dibCount(product.getDibCount())
//                .createdAt(product.getCreatedAt().toString())
//                .build();
//
//        productSearchRepository.save(doc);
//    }
//
//
//    /**
//     * 상품 검색
//     *
//     * @param keyword 검색어
//     * @return 검색된 상품 목록
//     */
//    public List<ProductDocument> search(String keyword) {
//        try {
//            SearchRequest request = SearchRequest.of(s -> s
//                    .index("products")
//                    .query(q -> q
//                            .matchPhrasePrefix(m -> m
//                                    .field("title")
//                                    .query(keyword)
//                            )
//                    )
//            );
//
//            SearchResponse<ProductDocument> response =
//                    elasticsearchClient.search(request, ProductDocument.class);
//
//            return response.hits().hits().stream()
//                    .map(Hit::source)
//                    .filter(Objects::nonNull)
//                    .toList();
//
//        } catch (IOException e) {
//            e.printStackTrace();
//            throw new CustomException(ProductSearchErrorCode.SEARCH_FAILED);
//        }
//    }
//
//    /**
//     * 상품 색인 삭제
//     *
//     * @param productId 삭제할 상품 ID
//     */
//    public void delete(Long productId) {
//        productSearchRepository.deleteById(productId.toString());
//    }
//}