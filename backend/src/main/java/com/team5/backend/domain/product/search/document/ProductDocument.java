package com.team5.backend.domain.product.search.document;

import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.elasticsearch.annotations.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document(indexName = "products")
@Data
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProductDocument {
    @Id
    private String id;

    private Long categoryId;
    private String title;
    private String description;
    private List<String> imageUrl;
    private Integer price;
    private Long dibCount;
    private String createdAt; // ES는 LocalDateTime을 못받음

    public LocalDateTime getCreatedAtAsDateTime() {
        return LocalDateTime.parse(this.createdAt);
    }
}
