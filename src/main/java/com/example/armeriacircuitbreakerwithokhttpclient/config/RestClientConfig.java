/*
 * Copyright (c) 2021 LINE Corporation. All rights reserved.
 * LINE Corporation PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.example.armeriacircuitbreakerwithokhttpclient.config;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;

@Configuration
public class RestClientConfig implements RestTemplateCustomizer {

    @Override
    public void customize(RestTemplate restTemplate) {
        restTemplate.getInterceptors().add((req, body, execution) -> {
            req.getHeaders().add(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_VALUE);
            return execution.execute(req, body);
        });
    }

    @Bean
    public OkHttpClient okHttpClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        return builder
                .connectTimeout(Duration.ofMillis(1000))
                .readTimeout(Duration.ofMillis(3000))
                .callTimeout(Duration.ofMillis(3000))
                .connectionPool(new ConnectionPool(50, 5000,
                                                   TimeUnit.MILLISECONDS))
                .build();
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder,
                                     OkHttpClient okHttpClient,
                                     OkHttpRetryInterceptor okHttpRetryInterceptor) {
        return builder
                .requestFactory(() -> {
                    OkHttpClient httpClient = okHttpClient.newBuilder()
                                                          .addInterceptor(okHttpRetryInterceptor)
                                                          .connectionPool(new ConnectionPool(0, 1,
                                                                                             TimeUnit.MILLISECONDS))
                                                          .connectTimeout(Duration.ofMillis(1000))
                                                          .readTimeout(Duration.ofMillis(3000))
                                                          .build();
                    OkHttp3ClientHttpRequestFactory clientHttpRequestFactory
                            = new OkHttp3ClientHttpRequestFactory(httpClient);
                    return clientHttpRequestFactory;
                })
                .build();
    }

    @Bean
    public OkHttpRetryInterceptor retryOkHttpInterceptor() {
        return new OkHttpRetryInterceptor(5);
    }
}
