package com.reactivespring.controller.v1;

import com.reactivespring.document.ItemCapped;
import com.reactivespring.repository.ItemCappedRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureWebTestClient
@DirtiesContext
class ItemStreamControllerTest {

    @Autowired
    ItemCappedRepository itemCappedRepository;

    @Autowired
    ReactiveMongoOperations mongoOperations;

    @Autowired
    WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        mongoOperations.dropCollection(ItemCapped.class);
        mongoOperations.createCollection(ItemCapped.class, CollectionOptions.empty().maxDocuments(20).size(50000).capped())
                .block();

        Flux<ItemCapped> itemCappedFlux = Flux.interval(Duration.ofSeconds(1))
                .map(i -> new ItemCapped(null, "Item " + i , (100.00 + i)))
                .take(5);

        itemCappedRepository
                .insert(itemCappedFlux)
                .doOnNext((itemCapped -> {
                    System.out.println("Inserted Item in setup: " + itemCapped);
                })).blockLast();
    }

    @Test
    void getItemStream() {
       Flux<ItemCapped> itemCappedFlux = webTestClient.get().uri("/stream/items")
                .exchange()
                .expectStatus().isOk()
                .returnResult(ItemCapped.class)
                .getResponseBody()
                .take(5);

        StepVerifier.create(itemCappedFlux)
                .expectNextCount(5)
                .thenCancel()
                .verify();
    }
}