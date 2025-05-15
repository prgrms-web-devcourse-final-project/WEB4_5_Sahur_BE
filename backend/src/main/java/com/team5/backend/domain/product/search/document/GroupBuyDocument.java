package com.team5.backend.domain.product.search.document;

import lombok.*;
import org.springframework.data.elasticsearch.annotations.Document;

import java.util.List;

@Document(indexName = "groupbuys")
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
