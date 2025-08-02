package com.gtalent.tutor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class HelloController {

    @GetMapping("/hello")
    public String hello () {
        return "Hello, World";
    }

    @GetMapping("/hello2")
    public String hello2 () {
        return "hello2.html";
    }
    @GetMapping("/vl/rest/datastore/F-C0032-001")
    public String forecastWeather() {
        return "台中市明天晴朗";
    }

    @PostMapping("/post")
    public String postTest() {
        return "post";
    }
}
