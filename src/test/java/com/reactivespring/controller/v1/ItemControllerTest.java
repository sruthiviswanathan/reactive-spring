package com.reactivespring.controller.v1;

import com.reactivespring.document.Item;
import com.reactivespring.repository.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ClientHttpRequest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Flux;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureWebTestClient
@DirtiesContext
class ItemControllerTest {

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    ItemRepository itemRepository;

    List<Item> itemList = Arrays.asList(
            Item.builder().description("TV").price(300.00).build(),
            Item.builder().description("Fridge").price(400.00).build(),
            Item.builder().description("Washer").price(600.00).build(),
            Item.builder().id("WACT123").description("Watch").price(600.00).build()
    );

    @BeforeEach
    void setUp() {
        itemRepository.deleteAll()
                .thenMany(Flux.fromIterable(itemList))
                .flatMap(itemRepository::save)
                .doOnNext(item -> System.out.println("Item Saved: " + item))
                .blockLast();
    }

    @Test
    void getAllItems() {
        webTestClient.get().uri("/items")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Item.class)
                .hasSize(4);
    }

    @Test
    void saveItem() {
        webTestClient.post().uri("/item")
                .body(BodyInserters.fromValue(Item.builder().description("BT Speakers").price(4000.00).build()))
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.description").isEqualTo("BT Speakers")
                .jsonPath("$.price").isEqualTo(4000.00);
    }

    @Test
    void updateItem() {
        webTestClient.put().uri("/item/WACT123")
                .body(BodyInserters.fromValue(Item.builder().description("Apple Watch").price(20000.00).build()))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.description").isEqualTo("Apple Watch")
                .jsonPath("$.price").isEqualTo(20000.00);
    }

    @Test
    void updateItemWithInvalidId() {
        webTestClient.put().uri("/item/WACT")
                .body(BodyInserters.fromValue(Item.builder().description("Apple Watch").price(20000.00).build()))
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void deleteItem() {
        webTestClient.delete().uri("/item/WACT123")
                .exchange()
                .expectStatus().isOk()
                .expectBody(Void.class);
    }
}