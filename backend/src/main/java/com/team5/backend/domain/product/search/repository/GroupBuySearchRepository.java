package com.team5.backend.domain.product.search.repository;

import com.team5.backend.domain.product.search.document.GroupBuyDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface GroupBuySearchRepository extends ElasticsearchRepository<GroupBuyDocument, String> {
}
