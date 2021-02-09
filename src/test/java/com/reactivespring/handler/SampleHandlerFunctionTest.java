package com.reactivespring.handler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;


import java.util.List;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest // Scans the classes with component configuration etc
@AutoConfigureWebTestClient // Configures WebTestClient Bean
class SampleHandlerFunctionTest {

    @Autowired
    WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
    }

    @Test
    void flux() {

        List<Integer> expectedInteger = Arrays.asList(1,2,3,4);

        webTestClient.get().uri("/functional/flux")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Integer.class)
                .consumeWith((response) -> assertEquals(expectedInteger, response.getResponseBody()));
    }

    @Test
    void mono() {

        List<String> expectedList = Arrays.asList("Hello World");

        webTestClient.get().uri("/functional/mono")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(String.class)
                .consumeWith((response) -> assertEquals(expectedList, response.getResponseBody()));
    }
}