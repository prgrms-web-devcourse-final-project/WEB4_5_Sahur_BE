package com.team5.backend.domain.product.search.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.team5.backend.domain.groupBuy.entity.GroupBuy;
import com.team5.backend.domain.groupBuy.entity.GroupBuyStatus;
import com.team5.backend.domain.groupBuy.dto.GroupBuyResDto;
import com.team5.backend.domain.product.dto.ProductDto;
import com.team5.backend.domain.product.search.document.GroupBuyDocument;
import com.team5.backend.domain.product.search.repository.GroupBuySearchRepository;
import com.team5.backend.global.exception.CustomException;
import com.team5.backend.global.exception.code.ProductSearchErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class GroupBuySearchService {

    private final ElasticsearchClient elasticsearchClient;
    private final GroupBuySearchRepository groupBuySearchRepository;

    /**
     * 색인
     */
    public void index(GroupBuy groupBuy) {
        GroupBuyDocument doc = GroupBuyDocument.builder()
                .id(groupBuy.getGroupBuyId().toString())
                .title(groupBuy.getProduct().getTitle())
                .description(groupBuy.getProduct().getDescription())
                .imageUrl(groupBuy.getProduct().getImageUrl())
                .price(groupBuy.getProduct().getPrice())
                .targetParticipants(groupBuy.getTargetParticipants())
                .currentParticipantCount(groupBuy.getCurrentParticipantCount())
                .round(groupBuy.getRound())
                .deadline(groupBuy.getDeadline().toString())
                .status(groupBuy.getStatus().toString())
                .createdAt(groupBuy.getCreatedAt().toString())
                .build();

        groupBuySearchRepository.save(doc);
    }

    /**
     * 검색
     */
    public List<GroupBuyResDto> search(String keyword) {
        try {
            SearchRequest request = SearchRequest.of(s -> s
                    .index("groupbuys")
                    .query(q -> q
                            .matchPhrasePrefix(m -> m
                                    .field("title")
                                    .query(keyword)
                            )
                    )
            );

            SearchResponse<GroupBuyDocument> response =
                    elasticsearchClient.search(request, GroupBuyDocument.class);

            return response.hits().hits().stream()
                    .map(Hit::source)
                    .filter(Objects::nonNull)
                    .map(this::toDto)
                    .toList();

        } catch (IOException e) {
            e.printStackTrace();
            throw new CustomException(ProductSearchErrorCode.SEARCH_FAILED);
        }
    }

    /**
     * 색인 삭제
     */
    public void delete(Long groupBuyId) {
        groupBuySearchRepository.deleteById(groupBuyId.toString());
    }

    /**
     * 변환 로직
     */
    private GroupBuyResDto toDto(GroupBuyDocument doc) {
        LocalDateTime deadline = LocalDateTime.parse(doc.getDeadline());
        boolean isDeadlineToday = deadline.toLocalDate().isEqual(LocalDateTime.now().toLocalDate());

        return GroupBuyResDto.builder()
                .groupBuyId(Long.parseLong(doc.getId()))
                .product(ProductDto.builder()
                        .title(doc.getTitle())
                        .description(doc.getDescription())
                        .price(doc.getPrice())
                        .imageUrl(doc.getImageUrl())
                        .build())
                .targetParticipants(doc.getTargetParticipants())
                .currentParticipantCount(doc.getCurrentParticipantCount())
                .round(doc.getRound())
                .deadline(deadline)
                .status(GroupBuyStatus.valueOf(doc.getStatus()))
                .isDeadlineToday(isDeadlineToday)
                .build();
    }
}
