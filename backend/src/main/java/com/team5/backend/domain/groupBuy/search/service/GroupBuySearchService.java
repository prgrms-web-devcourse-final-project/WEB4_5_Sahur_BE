package com.team5.backend.domain.groupBuy.search.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.team5.backend.domain.category.dto.CategoryDto;
import com.team5.backend.domain.category.entity.CategoryType;
import com.team5.backend.domain.category.entity.KeywordType;
import com.team5.backend.domain.groupBuy.dto.GroupBuyResDto;
import com.team5.backend.domain.groupBuy.entity.GroupBuy;
import com.team5.backend.domain.groupBuy.entity.GroupBuyStatus;
import com.team5.backend.domain.groupBuy.search.document.GroupBuyDocument;
import com.team5.backend.domain.groupBuy.search.repository.GroupBuySearchRepository;
import com.team5.backend.domain.product.dto.ProductDto;
import com.team5.backend.global.exception.CustomException;
import com.team5.backend.global.exception.code.ProductSearchErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
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
                .productId(groupBuy.getProduct().getProductId())
                .dibCount(groupBuy.getProduct().getDibCount())
                .categoryId(groupBuy.getProduct().getCategory().getCategoryId())
                .categoryType(groupBuy.getProduct().getCategory().getCategoryType().toString())
                .keyword(groupBuy.getProduct().getCategory().getKeyword().toString())
                .uid(groupBuy.getProduct().getCategory().getUid())
                .build();

        groupBuySearchRepository.save(doc);
        log.info("📦 색인 완료: groupBuyId={}", groupBuy.getGroupBuyId());
    }

    /**
     * 검색
     */
    public List<GroupBuyResDto> search(String keyword) {
        try {
            SearchRequest request = SearchRequest.of(s -> s
                    .index("groupbuy")
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
                    .map(this::toDto) // ← 여기서 DTO로 변환
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

    private GroupBuyResDto toDto(GroupBuyDocument doc) {
        LocalDateTime deadline = parseSafeDateTime(doc.getDeadline());
        boolean isDeadlineToday = deadline != null && deadline.toLocalDate().isEqual(LocalDateTime.now().toLocalDate());

        return GroupBuyResDto.builder()
                .groupBuyId(Long.parseLong(doc.getId()))
                .product(ProductDto.builder()
                        .productId(doc.getProductId())
                        .title(doc.getTitle())
                        .description(doc.getDescription())
                        .price(doc.getPrice())
                        .dibCount(doc.getDibCount())
                        .createdAt(parseSafeDateTime(doc.getCreatedAt()))
                        .imageUrl(doc.getImageUrl())
                        .category(doc.getCategoryId() != null ? new CategoryDto(
                                doc.getCategoryId(),
                                safeEnum(CategoryType.class, doc.getCategoryType()),
                                safeEnum(KeywordType.class, doc.getKeyword()),
                                doc.getUid()
                        ) : null)
                        .build())
                .targetParticipants(doc.getTargetParticipants())
                .currentParticipantCount(doc.getCurrentParticipantCount())
                .round(doc.getRound())
                .deadline(deadline)
                .status(GroupBuyStatus.valueOf(doc.getStatus()))
                .isDeadlineToday(isDeadlineToday)
                .build();
    }

    private LocalDateTime parseSafeDateTime(String date) {
        try {
            return date != null ? LocalDateTime.parse(date) : null;
        } catch (Exception e) {
            log.warn("⚠️ 날짜 파싱 실패: {}", date);
            return null;
        }
    }

    private <T extends Enum<T>> T safeEnum(Class<T> enumClass, String value) {
        try {
            return value != null ? Enum.valueOf(enumClass, value) : null;
        } catch (Exception e) {
            log.warn("⚠️ Enum 파싱 실패: {} → {}", enumClass.getSimpleName(), value);
            return null;
        }
    }
}
