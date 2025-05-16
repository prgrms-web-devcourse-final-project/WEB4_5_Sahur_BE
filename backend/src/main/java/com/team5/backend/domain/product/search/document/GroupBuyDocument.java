package com.team5.backend.domain.product.search.document;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.springframework.data.elasticsearch.annotations.Document;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "groupbuy")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupBuyDocument {
    private String id; // groupBuyId
    private String title;
    private String description;
    private List<String> imageUrl;
    private int price;

    private int targetParticipants;
    private int currentParticipantCount;
    private int round;
    private String deadline;
    private String status; // GroupBuyStatus.toString()
    private String createdAt;
}
