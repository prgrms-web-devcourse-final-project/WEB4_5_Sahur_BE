package com.team5.backend.domain.groupBuy.search.service;

import com.team5.backend.domain.groupBuy.dto.GroupBuyResDto;
import com.team5.backend.domain.groupBuy.entity.GroupBuy;
import com.team5.backend.domain.groupBuy.search.repository.GroupBuyQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GroupBuyDbSearchService {

    private final GroupBuyQueryRepository groupBuyQueryRepository;

    @Transactional(readOnly = true)
    public List<GroupBuyResDto> search(String keyword) {
        List<GroupBuy> groupBuys = groupBuyQueryRepository.searchByProductTitle(keyword);

        return groupBuys.stream()
                .map(gb -> GroupBuyResDto.fromEntity(gb,
                        gb.getDeadline().toLocalDate().isEqual(LocalDate.now())))
                .toList();
    }
}