package com.team5.backend.domain.product.search.repository;

import com.team5.backend.domain.product.search.document.ProductDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface ProductSearchRepository extends ElasticsearchRepository<ProductDocument, String> {
    List<ProductDocument> findByTitleContainingOrDescriptionContaining(String title, String description);
}

