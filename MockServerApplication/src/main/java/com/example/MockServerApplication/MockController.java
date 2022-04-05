package com.example.MockServerApplication;

import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MockController {
    private static final AtomicInteger REQ_CNT = new AtomicInteger();
    @GetMapping("/world")
    ResponseEntity<String> world() {
        if(REQ_CNT.addAndGet(1) % 2 == 0) {
            return ResponseEntity.ok().body("success");
        } else {
            return ResponseEntity.internalServerError().body("fail");
        }
    }
}
