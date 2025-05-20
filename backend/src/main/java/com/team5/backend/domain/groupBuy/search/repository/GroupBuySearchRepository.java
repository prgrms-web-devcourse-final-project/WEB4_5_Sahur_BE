package com.team5.backend.domain.groupBuy.search.repository;

import com.team5.backend.domain.groupBuy.search.document.GroupBuyDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface GroupBuySearchRepository extends ElasticsearchRepository<GroupBuyDocument, String> {
}
