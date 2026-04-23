package com.maicorder.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

@Configuration
public class WebClientConfig {

    @Value("${maimaipy-base-url}") String maimaipy_BaseUrl;

    @Bean
    @Qualifier("maimaiClient")
    public WebClient maimaiClient() {
        HttpClient httpClient = HttpClient.create()
                .compress(true)
                .responseTimeout(Duration.ofSeconds(10))
                .followRedirect(true);

        return WebClient.builder()
                .baseUrl(maimaipy_BaseUrl)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }

    /**
     * 通用 WebClient（无特殊配置，用于其他公开 API）
     */
    @Bean
    @Qualifier("defaultClient")
    public WebClient defaultClient(WebClient.Builder builder) {
        return builder.build();
    }
}
