//package com.team5.backend.domain.product.search.document;
//
//import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
//import jakarta.persistence.Id;
//import lombok.*;
//import org.springframework.data.elasticsearch.annotations.Document;
//
//import java.util.List;
//
//@JsonIgnoreProperties(ignoreUnknown = true)
//@Document(indexName = "products")
//@Data
//@Builder
//@Getter
//@NoArgsConstructor
//@AllArgsConstructor
//public class ProductDocument {
//    @Id
//    private String id;
//
//    private Long categoryId;
//    private String title;
//    private String description;
//    private List<String> imageUrl;
//    private Integer price;
//    private Long dibCount;
//    private String createdAt;
//}
