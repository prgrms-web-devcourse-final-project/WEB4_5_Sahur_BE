package com.team5.backend.domain.product.search.service;

import com.team5.backend.domain.product.entity.Product;
import com.team5.backend.domain.product.search.document.ProductDocument;
import com.team5.backend.domain.product.search.repository.ProductSearchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductSearchService {

    private final ProductSearchRepository productSearchRepository;

    public void index(Product product) {
        ProductDocument doc = ProductDocument.builder()
                .id(product.getProductId().toString())
                .categoryId(product.getCategory().getCategoryId())
                .title(product.getTitle())
                .description(product.getDescription())
                .imageUrl(product.getImageUrl())
                .price(product.getPrice())
                .dibCount(product.getDibCount())
                .createdAt(product.getCreatedAt())
                .build();

        productSearchRepository.save(doc);
    }

    public List<ProductDocument> search(String keyword) {
        return productSearchRepository.findByTitleContainingOrDescriptionContaining(keyword, keyword);
    }
}