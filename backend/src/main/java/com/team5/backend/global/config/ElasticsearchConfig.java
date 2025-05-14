package com.team5.backend.global.config;

//@Configuration
//public class ElasticsearchConfig {
//
//    @Value("${custom.db.host}")
//    private String elasticsearchHost;
//
//    @Bean
//    public ElasticsearchClient elasticsearchClient() {
//        RestClient restClient = RestClient.builder(
//                new HttpHost(elasticsearchHost, 9200, "http")
//        ).build();
//
//        ElasticsearchTransport transport = new RestClientTransport(
//                restClient, new JacksonJsonpMapper()
//        );
//
//        return new ElasticsearchClient(transport);
//    }
//}

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;

@Configuration
public class ElasticsearchConfig extends ElasticsearchConfiguration {

    @Value("${custom.elastic.host}")
    private String elasticsearchHost;

    @Override
    public ClientConfiguration clientConfiguration() {
        return ClientConfiguration.builder()
                .connectedTo(elasticsearchHost + ":9200")
                .build();
    }
}