package com.team5.backend.domain.groupBuy.search.service;

import com.team5.backend.domain.groupBuy.repository.GroupBuyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GroupBuyReindexService {

    private final GroupBuyRepository groupBuyRepository;
    private final GroupBuySearchService groupBuySearchService;

    
    @Transactional(readOnly = true)
    public void reindexAll() {
        groupBuyRepository.findAll()
                .forEach(groupBuySearchService::index);
    }
}
