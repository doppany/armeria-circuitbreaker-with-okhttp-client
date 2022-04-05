package com.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.armeriacircuitbreakerwithokhttpclient.MainService;

@RestController
public class MainController {
    @Autowired
    private MainService mainService;
    @GetMapping("/get")
    String get(){
        mainService.requestToMock();
        return "Requested to mock api successfully";
    }
}
