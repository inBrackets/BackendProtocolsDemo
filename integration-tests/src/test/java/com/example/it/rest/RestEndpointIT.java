package com.example.it.rest;

import com.example.it.dto.HelloResponse;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;

class RestEndpointIT {

    @BeforeAll
    static void setUp() {
        RestAssured.baseURI = System.getProperty("rest.base-url", "http://localhost:8080");
    }

    @Test
    void shouldReturnHelloWorldMessage() {
        given()
        .when()
            .get("/api/hello")
        .then()
            .statusCode(200)
            .body("message", equalTo("Hello World!"));
    }

    @Test
    void shouldDeserializeResponseAndVerifyFields() {
        HelloResponse response = given()
        .when()
            .get("/api/hello")
        .then()
            .statusCode(200)
            .extract()
            .as(HelloResponse.class);

        assertThat(response).isNotNull();
        assertThat(response.message()).isEqualTo("Hello World!");
    }
}
