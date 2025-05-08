package com.team5.backend.domain.product.search.document;

import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.elasticsearch.annotations.Document;

import java.time.LocalDateTime;

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
    private String imageUrl;
    private Integer price;
    private Long dibCount;
    private LocalDateTime createdAt;
}
