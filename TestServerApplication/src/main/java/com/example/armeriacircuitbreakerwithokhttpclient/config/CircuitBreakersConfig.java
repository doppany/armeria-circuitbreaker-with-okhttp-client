package com.example.armeriacircuitbreakerwithokhttpclient.config;

import java.time.Duration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.linecorp.armeria.client.circuitbreaker.CircuitBreaker;

@Configuration
public class CircuitBreakersConfig {
    @Bean
    public static CircuitBreaker testCircuitBreaker() {
        return CircuitBreaker
                .builder("test-armeria-circuit-breaker")
                .counterSlidingWindow(Duration.ofSeconds(20))
                .circuitOpenWindow(Duration.ofSeconds(5))
                .failureRateThreshold(0.3)
                .minimumRequestThreshold(5)
                .trialRequestInterval(Duration.ofSeconds(3))
                .build();
    }
}
