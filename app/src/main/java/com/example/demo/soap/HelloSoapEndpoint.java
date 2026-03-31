package com.example.demo.soap;

import com.example.demo.service.HelloService;
import com.example.demo.soap.generated.GetHelloRequest;
import com.example.demo.soap.generated.GetHelloResponse;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

@Endpoint
public class HelloSoapEndpoint {

    private static final String NAMESPACE = "http://example.com/demo/soap";

    private final HelloService helloService;

    public HelloSoapEndpoint(HelloService helloService) {
        this.helloService = helloService;
    }

    @PayloadRoot(namespace = NAMESPACE, localPart = "getHelloRequest")
    @ResponsePayload
    public GetHelloResponse getHello(@RequestPayload GetHelloRequest request) {
        GetHelloResponse response = new GetHelloResponse();
        response.setMessage(helloService.getGreeting());
        return response;
    }
}
