package com.reactivespring.handler;

import com.reactivespring.document.Item;
import com.reactivespring.repository.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Flux;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureWebTestClient
@DirtiesContext
class ItemsHandlerFunctionTest {


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
        webTestClient.get().uri("/func/items")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Item.class)
                .hasSize(4);
    }

    @Test
    void getItemsById() {
        webTestClient.get().uri("/func/item/WACT123")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Item.class);
    }

    @Test
    void getItemsByIdNotFound() {
        webTestClient.get().uri("/func/item/WACT")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void saveItem() {
        webTestClient.post().uri("/func/item")
                .body(BodyInserters.fromValue(Item.builder().description("BT Speakers").price(3000.00).build()))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.description").isEqualTo("BT Speakers")
                .jsonPath("$.price").isEqualTo(3000.00);
    }

    @Test
    void deleteItem() {
        webTestClient.delete().uri("/func/item/WACT123")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Void.class);
    }

    @Test
    void updateItem() {
        webTestClient.put().uri("/func/item/WACT123")
                .body(BodyInserters.fromValue(Item.builder().description("Apple Watch").price(12000.00).build()))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Item.class);
    }

    @Test
    void updateItemNotFound() {
        webTestClient.put().uri("/func/item/WACT")
                .body(BodyInserters.fromValue(Item.builder().description("Apple Watch").price(12000.00).build()))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void testRunTimeException() {
        webTestClient.get().uri("/func/runtimeException")
                .exchange()
                .expectStatus().is5xxServerError();
    }
}