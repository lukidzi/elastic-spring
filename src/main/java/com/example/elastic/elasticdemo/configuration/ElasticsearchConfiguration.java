package com.example.elastic.elasticdemo.configuration;

import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Configuration
public class ElasticsearchConfiguration {

    @Value("${elastic.server}")
    private String host;

    @Value("${elastic.port}")
    private int port;

    @Value("${elastic.cluster}")
    private String cluster;

    @Bean
    Client getClient() throws UnknownHostException {
        Settings settings = Settings.builder()
                .put("cluster.name", cluster).build();
        return new PreBuiltTransportClient(settings)
                .addTransportAddress(new TransportAddress(InetAddress.getByName(host), port));
    }
}
