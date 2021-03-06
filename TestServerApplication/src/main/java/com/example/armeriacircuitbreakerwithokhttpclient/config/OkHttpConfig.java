package com.example.armeriacircuitbreakerwithokhttpclient.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import lombok.Data;

@Data
@Validated
@Configuration
public class OkHttpConfig {
    private int connectTimeout;
    private int readTimeout;
    private int writeTimeout;
}