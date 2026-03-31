package com.example.it.soap;

import com.example.it.soap.generated.GetHelloRequest;
import com.example.it.soap.generated.GetHelloResponse;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.client.core.WebServiceTemplate;

import static org.assertj.core.api.Assertions.assertThat;

class SoapEndpointIT {

    private static final String BASE_URL = System.getProperty("rest.base-url", "http://localhost:8080");

    private static WebServiceTemplate webServiceTemplate;

    @BeforeAll
    static void setUp() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setContextPath("com.example.it.soap.generated");

        webServiceTemplate = new WebServiceTemplate(marshaller);
        webServiceTemplate.setDefaultUri(BASE_URL + "/soap");
    }

    @Test
    void shouldReturnHelloWorldMessage() {
        GetHelloRequest request = new GetHelloRequest();
        request.setName("World");

        GetHelloResponse response = (GetHelloResponse) webServiceTemplate.marshalSendAndReceive(request);

        assertThat(response).isNotNull();
        assertThat(response.getMessage()).isEqualTo("Hello World!");
    }
}
