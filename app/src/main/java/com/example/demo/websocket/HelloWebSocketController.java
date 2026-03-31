package com.example.demo.websocket;

import com.example.demo.dto.HelloResponse;
import com.example.demo.service.HelloService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class HelloWebSocketController {

    private final HelloService helloService;
    private final SimpMessagingTemplate messagingTemplate;

    public HelloWebSocketController(HelloService helloService, SimpMessagingTemplate messagingTemplate) {
        this.helloService = helloService;
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/hello")
    public void streamHello() {
        new Thread(() -> {
            int index = 1;
            while (!Thread.currentThread().isInterrupted()) {
                messagingTemplate.convertAndSend("/topic/greetings",
                        new HelloResponse(helloService.getStreamGreeting(index++)));
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }).start();
    }
}
