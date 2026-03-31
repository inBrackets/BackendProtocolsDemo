package com.example.demo.service;

import org.springframework.stereotype.Service;

@Service
public class HelloService {

    private static final String GREETING = "Hello World!";
    private static final String STREAM_GREETING_FORMAT = "Hello World - %d";

    public String getGreeting() {
        return GREETING;
    }

    public String getStreamGreeting(int index) {
        return String.format(STREAM_GREETING_FORMAT, index);
    }
}
