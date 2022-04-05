package com.example.armeriacircuitbreakerwithokhttpclient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class MainService {
    @Autowired
    private RestTemplate restTemplate;
    public void requestToMock() {
        ResponseEntity<String> world = restTemplate.getForEntity("http://localhost:8797/world", String.class);
        log.info("isWorldAlive: {}", world);
    }
}
