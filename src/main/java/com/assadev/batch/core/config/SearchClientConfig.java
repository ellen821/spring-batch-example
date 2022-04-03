package com.assadev.batch.core.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SearchClientConfig {

    @Bean
    @ConfigurationProperties(prefix = "indexer.search.es", ignoreInvalidFields = true)
    public ElasticsearchProperties searchClusterProperties() {
        return new ElasticsearchProperties();
    }

    @Bean
    public ElasticsearchClient searchClient() {
        ElasticsearchProperties elasticsearchProperties = searchClusterProperties();
        RestClient restClient = RestClient.builder(new HttpHost(elasticsearchProperties.getHost(),
                        elasticsearchProperties.getPort(), elasticsearchProperties.getHttpProtocol()))
                .build();

        ElasticsearchTransport transport = new RestClientTransport(
                restClient, new JacksonJsonpMapper());

        return new ElasticsearchClient(transport);
    }
//    @Bean
//    public ElasticsearchClient searchClient() {
//        ElasticsearchProperties elasticsearchProperties = searchClusterProperties();
//
//        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
//        credentialsProvider.setCredentials(AuthScope.ANY,
//                new UsernamePasswordCredentials(elasticsearchProperties.getUserName(),
//                        elasticsearchProperties.getPassword()));
//
//        RestClientBuilder builder = RestClient.builder(
//                        new HttpHost(elasticsearchProperties.getHost(), elasticsearchProperties.getPort(),
//                                elasticsearchProperties.getHttpProtocol()))
//                .setHttpClientConfigCallback(httpAsyncClientBuilder -> httpAsyncClientBuilder
//                        .setDefaultCredentialsProvider(credentialsProvider));
//
//        ElasticsearchTransport transport = new RestClientTransport(
//                builder.build(), new JacksonJsonpMapper());
//
//        return new ElasticsearchClient(transport);
//    }


}
