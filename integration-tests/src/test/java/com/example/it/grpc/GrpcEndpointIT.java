package com.example.it.grpc;

import com.example.demo.grpc.generated.HelloReply;
import com.example.demo.grpc.generated.HelloRequest;
import com.example.demo.grpc.generated.HelloServiceGrpc;
import com.example.it.dto.HelloResponse;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

class GrpcEndpointIT {

    private static final String HOST = System.getProperty("grpc.host", "localhost");
    private static final int PORT = Integer.parseInt(System.getProperty("grpc.port", "9090"));
    private static final int MESSAGES_TO_COLLECT = 3;

    private static ManagedChannel channel;
    private static HelloServiceGrpc.HelloServiceBlockingStub blockingStub;
    private static HelloServiceGrpc.HelloServiceStub asyncStub;

    @BeforeAll
    static void setUp() {
        channel = ManagedChannelBuilder.forAddress(HOST, PORT)
                .usePlaintext()
                .build();
        blockingStub = HelloServiceGrpc.newBlockingStub(channel);
        asyncStub = HelloServiceGrpc.newStub(channel);
    }

    @AfterAll
    static void tearDown() throws InterruptedException {
        if (channel != null) {
            channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
        }
    }

    @Test
    void shouldReturnHelloWorldForUnaryCall() {
        HelloResponse response = toDto(blockingStub.sayHello(HelloRequest.newBuilder().build()));

        assertThat(response).isNotNull();
        assertThat(response.message()).isEqualTo("Hello World!");
    }

    @Test
    void shouldStreamEnumeratedHelloMessages() throws Exception {
        List<HelloResponse> responses = new CopyOnWriteArrayList<>();
        CountDownLatch latch = new CountDownLatch(MESSAGES_TO_COLLECT);

        io.grpc.Context.CancellableContext context = io.grpc.Context.current().withCancellation();
        context.run(() -> asyncStub.sayHelloStream(
                HelloRequest.newBuilder().build(),
                new StreamObserver<>() {
                    @Override
                    public void onNext(HelloReply reply) {
                        responses.add(toDto(reply));
                        latch.countDown();
                    }

                    @Override
                    public void onError(Throwable t) {}

                    @Override
                    public void onCompleted() {}
                }
        ));

        assertThat(latch.await(10, TimeUnit.SECONDS))
                .as("Should receive at least %d messages", MESSAGES_TO_COLLECT)
                .isTrue();

        context.cancel(null);

        assertThat(responses).hasSizeGreaterThanOrEqualTo(MESSAGES_TO_COLLECT);
        assertThat(responses)
                .extracting(HelloResponse::message)
                .allMatch(msg -> msg.matches("Hello World - \\d+"));

        for (int i = 0; i < responses.size(); i++) {
            assertThat(responses.get(i).message()).isEqualTo("Hello World - " + (i + 1));
        }
    }

    private static HelloResponse toDto(HelloReply reply) {
        return new HelloResponse(reply.getMessage());
    }
}
