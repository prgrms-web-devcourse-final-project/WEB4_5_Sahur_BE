package com.team5.backend.domain.product.search.service;

import com.team5.backend.domain.product.repository.ProductRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductReindexService {

    private final ProductRepository productRepository;
    private final ProductSearchService productSearchService;

    @PostConstruct
    public void reindexAll() {
        productRepository.findAll()
                .forEach(productSearchService::index);
    }
}