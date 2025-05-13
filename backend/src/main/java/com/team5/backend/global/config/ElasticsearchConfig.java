//package com.team5.backend.global.config;
//
//import org.apache.http.HttpHost;
//import org.elasticsearch.client.RestClient;
//import org.elasticsearch.client.RestHighLevelClient;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class ElasticsearchConfig {
//
//    @Value("${spring.data.elasticsearch.url}")
//    private String elasticsearchHost;
//
//    @Bean
//    public RestClient restClient() {
//        return RestClient.builder(new HttpHost(elasticsearchHost, 9200, "http")).build();
//    }
//
//    @Bean
//    public RestHighLevelClient elasticsearchClient(RestClient restClient) {
//        return new RestHighLevelClient(RestClient.builder(new HttpHost(elasticsearchHost, 9200, "http")));
//    }
//}