package com.example.it.websocket;

import com.example.it.dto.HelloResponse;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

class WebSocketEndpointIT {

    private static final String BASE_URL = System.getProperty("rest.base-url", "http://localhost:8080");
    private static final String WS_URL = BASE_URL.replace("http", "ws") + "/ws-stomp";
    private static final int MESSAGES_TO_COLLECT = 3;

    private static WebSocketStompClient stompClient;
    private static StompSession session;

    @BeforeAll
    static void setUp() throws Exception {
        stompClient = new WebSocketStompClient(new StandardWebSocketClient());
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());

        session = stompClient
                .connectAsync(WS_URL, new StompSessionHandlerAdapter() {})
                .get(5, TimeUnit.SECONDS);
    }

    @AfterAll
    static void tearDown() {
        if (session != null && session.isConnected()) {
            session.disconnect();
        }
        if (stompClient != null) {
            stompClient.stop();
        }
    }

    @Test
    void shouldReceiveStreamedHelloMessages() throws Exception {
        List<HelloResponse> responses = new CopyOnWriteArrayList<>();
        CountDownLatch latch = new CountDownLatch(MESSAGES_TO_COLLECT);

        StompSession.Subscription subscription = session.subscribe(
                "/topic/greetings", new HelloResponseFrameHandler(responses, latch));
        session.send("/app/hello", "");

        assertThat(latch.await(10, TimeUnit.SECONDS))
                .as("Should receive at least %d messages", MESSAGES_TO_COLLECT)
                .isTrue();

        subscription.unsubscribe();

        assertThat(responses).hasSizeGreaterThanOrEqualTo(MESSAGES_TO_COLLECT);
        assertThat(responses)
                .extracting(HelloResponse::message)
                .allMatch(msg -> msg.matches("Hello World - \\d+"));

        for (int i = 0; i < responses.size(); i++) {
            assertThat(responses.get(i).message()).isEqualTo("Hello World - " + (i + 1));
        }
    }

    private record HelloResponseFrameHandler(
            List<HelloResponse> responses,
            CountDownLatch latch
    ) implements StompFrameHandler {

        @Override
        public Type getPayloadType(StompHeaders headers) {
            return HelloResponse.class;
        }

        @Override
        public void handleFrame(StompHeaders headers, Object payload) {
            responses.add((HelloResponse) payload);
            latch.countDown();
        }
    }
}
