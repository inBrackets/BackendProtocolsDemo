package com.example.demo.grpc;

import com.example.demo.grpc.generated.HelloReply;
import com.example.demo.grpc.generated.HelloRequest;
import com.example.demo.grpc.generated.HelloServiceGrpc;
import com.example.demo.service.HelloService;
import io.grpc.Context;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
public class HelloGrpcService extends HelloServiceGrpc.HelloServiceImplBase {

    private final HelloService helloService;

    public HelloGrpcService(HelloService helloService) {
        this.helloService = helloService;
    }

    @Override
    public void sayHello(HelloRequest request, StreamObserver<HelloReply> responseObserver) {
        HelloReply reply = HelloReply.newBuilder()
                .setMessage(helloService.getGreeting())
                .build();
        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }

    @Override
    public void sayHelloStream(HelloRequest request, StreamObserver<HelloReply> responseObserver) {
        Context context = Context.current();
        new Thread(() -> {
            int index = 1;
            while (!context.isCancelled()) {
                HelloReply reply = HelloReply.newBuilder()
                        .setMessage(helloService.getStreamGreeting(index++))
                        .build();
                responseObserver.onNext(reply);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
            responseObserver.onCompleted();
        }).start();
    }
}
